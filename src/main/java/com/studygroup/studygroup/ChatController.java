package com.studygroup.studygroup;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import javafx.scene.text.Text;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatController extends DatabaseConnection{
    public ChatController() throws SQLException {
        super();
    }
    @FXML
    private VBox messageBox;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private VBox groupsBox;

    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private String name;
    private volatile boolean running = true; // Used to control the ReadThread loop


    public ResultSet getUserGroupChats(int UserID) throws SQLException {
        sqlCommand = "SELECT * FROM ChatGroups JOIN GroupMemberships ON GroupMemberships.ChatGroupID = ChatGroups.ChatGroupID WHERE GroupMemberships.UserID = ?;";
        statement = con.prepareStatement(sqlCommand);
        statement.setInt(1, UserID);

        return statement.executeQuery();
    }

    public void initialize() throws SQLException {
        // Retrieve the user's group chats
        ResultSet resultGroupChats = getUserGroupChats(Home.UserID);

        if (resultGroupChats.next()) { // Check if there are results
            // Initialize the first chat group
            final String firstGroupAddress = resultGroupChats.getString("IP");
            final int firstGroupPort = resultGroupChats.getInt("PortNumber");
            initChat(firstGroupAddress, firstGroupPort); // Initialize with first group's IP and port

            // Add the first group's button
            Button firstGroupButton = new Button(resultGroupChats.getString("ChatGroupName"));
            firstGroupButton.setOnAction(e -> switchGroup(firstGroupAddress, firstGroupPort));
            groupsBox.getChildren().add(firstGroupButton);

            // Process remaining rows
            while (resultGroupChats.next()) {
                final String groupAddress = resultGroupChats.getString("IP");
                final int groupPort = resultGroupChats.getInt("PortNumber");

                Button button = new Button("ChatGroupName");
                button.setOnAction(e -> switchGroup(groupAddress, groupPort));
                groupsBox.getChildren().add(button);
            }
        } else {
            System.out.println("No groups found for the user. Skipping initChat.");
            // Optionally, add a placeholder message in the UI
        }

        // Set actions for sending messages
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());
    }

    private void initChat(String host, int port) {
        try {
            // Stop the previous thread and close the socket if necessary
            running = false;
            if (socket != null) {
                socket.leaveGroup(group);
                socket.close();
            }

            // Reset running for the new thread
            running = true;

            group = InetAddress.getByName(host);
            this.port = port;

            name = Home.Username;

            // Initialize the socket
            socket = new MulticastSocket(this.port);
            socket.setTimeToLive(1); // Local network
            socket.joinGroup(group);

            // Start the new message-reading thread
            new Thread(new ReadThread(socket, group, this.port)).start();

            // Notify the user

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendMessage(name + ": " + message, true);
            try {
                String fullMessage = name + ": " + message;

                byte[] buffer = fullMessage.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(packet);

                messageField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchGroup(String groupAddress, int port) {
        initChat(groupAddress, port);
    }

    private void appendMessage(String message, boolean isUserMessage) {
        Platform.runLater(() -> {
            // Create a new HBox for the message
            HBox messageBoxContainer = new HBox(10);

            Text messageText = new Text(message);
            messageText.getStyleClass().add(isUserMessage ? "chat-bubble-you" : "chat-bubble-other");

            if (isUserMessage) {
                messageBoxContainer.getChildren().add(messageText);
                messageBoxContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageBoxContainer.getChildren().add(messageText);
                messageBoxContainer.setAlignment(Pos.CENTER_LEFT);
            }

            // Add the new message box to the main message box
            messageBox.getChildren().add(messageBoxContainer);
        });
    }

    private class ReadThread implements Runnable {
        private final MulticastSocket socket;
        private final InetAddress group;
        private final int port;

        public ReadThread(MulticastSocket socket, InetAddress group, int port) {
            this.socket = socket;
            this.group = group;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1000];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                    socket.receive(packet);
                    String message = new String(buffer, 0, packet.getLength());

                    if (!message.startsWith(name)) {
                        appendMessage(message, false);
                    }
                }
            } catch (IOException e) {
                if (running) { // Avoid logging exceptions when closing
                    e.printStackTrace();
                }
            }
        }
    }


    public void stop() {
        running = false; // Stop the thread
        try {
            if (socket != null) {
                socket.leaveGroup(group);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
