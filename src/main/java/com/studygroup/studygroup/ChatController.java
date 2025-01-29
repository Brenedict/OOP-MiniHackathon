package com.studygroup.studygroup;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


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
    private String currentChatGroupID;
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
            currentChatGroupID = resultGroupChats.getString("ChatGroupID");
            final String firstGroupAddress = resultGroupChats.getString("IP");
            final int firstGroupPort = resultGroupChats.getInt("PortNumber");
            initChat(firstGroupAddress, firstGroupPort); // Initialize with first group's IP and port

            // Load chat history for the first group
            loadChatHistory(currentChatGroupID);

            // Add the first group's button
            Button firstGroupButton = new Button(resultGroupChats.getString("ChatGroupName"));
            firstGroupButton.setOnAction(e -> switchGroup(firstGroupAddress, firstGroupPort));
            groupsBox.getChildren().add(firstGroupButton);

            // Process remaining rows
            while (resultGroupChats.next()) {
                final String groupAddress = resultGroupChats.getString("IP");
                final int groupPort = resultGroupChats.getInt("PortNumber");

                Button button = new Button(resultGroupChats.getString("ChatGroupName"));
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

    private void loadChatHistory(String chatGroupID) {
        try {
            String sql = "SELECT credentials.Username, Messages.MessageText, Messages.Timestamp " +
                    "FROM Messages " +
                    "JOIN credentials ON Messages.UserID = credentials.UserID " +
                    "WHERE Messages.ChatGroupID = ? " +
                    "ORDER BY Messages.Timestamp ASC";

            statement = con.prepareStatement(sql);
            statement.setString(1, chatGroupID);
            resultSet = statement.executeQuery();

            messageBox.getChildren().clear(); // Clear existing messages

            while (resultSet.next()) {
                String sender = resultSet.getString("Username");
                String messageText = resultSet.getString("MessageText");
                appendMessage(sender, messageText, sender.equals(name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            appendMessage(name, message, true);
            try {
                // Send message over multicast
                String fullMessage = name + ": " + message;
                byte[] buffer = fullMessage.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(packet);

                // Save message to database
                String sql = "INSERT INTO Messages (ChatGroupID, UserID, MessageText) VALUES (?, ?, ?)";
                statement = con.prepareStatement(sql);
                statement.setString(1, currentChatGroupID);
                statement.setInt(2, Home.UserID);
                statement.setString(3, message);
                statement.executeUpdate();

                messageField.clear();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchGroup(String groupAddress, int port) {
        messageBox.getChildren().clear(); // Clear UI messages before switching

        // Find the corresponding ChatGroupID for the switched group
        try {
            String sql = "SELECT ChatGroupID FROM ChatGroups WHERE IP = ? AND PortNumber = ?";
            statement = con.prepareStatement(sql);
            statement.setString(1, groupAddress);
            statement.setInt(2, port);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                currentChatGroupID = rs.getString("ChatGroupID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initChat(groupAddress, port);
        loadChatHistory(currentChatGroupID); // Now loads messages for the correct group
    }

    private void appendMessage(String sender, String message, boolean isUserMessage) {
        Platform.runLater(() -> {
            try {
                // Load the chat bubble FXML
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("chat-bubble.fxml"));
                HBox chatBubble = loader.load();

                // Find the Label element from the loaded FXML
                Label messageLabel = (Label) chatBubble.lookup("#messageLabel");

                // Set the message text on the label
                messageLabel.setText(message);

                // Apply the correct style for user vs other messages
                if (isUserMessage) {
                    chatBubble.getStyleClass().add("chat-bubble-you");
                    chatBubble.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    chatBubble.getStyleClass().add("chat-bubble-other");
                    chatBubble.setAlignment(Pos.CENTER_LEFT);

                    // Add the sender's name on the left side
                    Text senderText = new Text(sender);  // Create a Text object for the sender's name
                    senderText.setStyle("-fx-font-weight: bold;");  // Optionally make the sender name bold
                    HBox senderBox = new HBox(senderText);
                    senderBox.setAlignment(Pos.CENTER_LEFT);

                    // Add sender's name above the message bubble
                    VBox messageWithSender = new VBox(senderBox, chatBubble);
                    messageBox.getChildren().add(messageWithSender);
                    return;  // Stop here since the layout with sender has been added
                }

                // Add the chat bubble to the message box (VBox or HBox)
                messageBox.getChildren().add(chatBubble);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveFileToDatabase(File file, byte[] fileContent) {
        try {
            String sql = "INSERT INTO Files (ChatGroupID, UserID, Filename, FileData) VALUES (?, ?, ?, ?)";
            statement = con.prepareStatement(sql);
            statement.setString(1, currentChatGroupID); // Using current chat group ID
            statement.setInt(2, Home.UserID);  // Using current user ID
            statement.setString(3, file.getName());  // Filename
            statement.setBytes(4, fileContent);  // File content as byte array
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToHome() throws IOException {
        Main.switchHomePage();
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
                        // Split the message into sender and message parts
                        String sender = message.split(":")[0];  // Assuming the format is "sender: message"
                        String messageContent = message.substring(sender.length() + 1);  // Get the actual message

                        appendMessage(sender, messageContent, false);  // False indicates this is a message from another user
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
