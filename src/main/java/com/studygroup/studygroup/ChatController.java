package com.studygroup.studygroup;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

            // Create the first group's button
            Button firstGroupButton = new Button(resultGroupChats.getString("ChatGroupName"));
            styleButton(firstGroupButton);
            firstGroupButton.setOnAction(e -> switchGroup(firstGroupAddress, firstGroupPort));
            groupsBox.getChildren().add(firstGroupButton);

            // Process remaining rows
            while (resultGroupChats.next()) {
                final String groupAddress = resultGroupChats.getString("IP");
                final int groupPort = resultGroupChats.getInt("PortNumber");

                // Create buttons for other groups
                Button button = new Button(resultGroupChats.getString("ChatGroupName"));
                styleButton(button);
                button.setOnAction(e -> switchGroup(groupAddress, groupPort));
                groupsBox.getChildren().add(button);
            }
        } else {
            System.out.println("No groups found for the user. Skipping initChat.");
            // Optionally, add a placeholder message in the UI
        }
    }

    // Method to style the buttons
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #ffdf00; "
                + "-fx-text-fill: black; "
                + "-fx-font-weight: bold; "   // Make text bold
                + "-fx-font-size: 14px; "
                + "-fx-padding: 10px 20px; "
                + "-fx-border-radius: 5px; "
                + "-fx-background-radius: 5px; "
                + "-fx-border-color: maroon; "  // Maroon border
                + "-fx-alignment: CENTER_LEFT;"); // Align text to the left
        button.setMaxWidth(Double.MAX_VALUE); // Make button width fill the VBox
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
            // Outer container for each message (VBox)
            VBox messageContainer = new VBox();
            messageContainer.setSpacing(5);

            // Add margin to each VBox to create space between the messages
            messageContainer.setMargin(messageContainer, new javafx.geometry.Insets(5, 0, 5, 0));

            // Add a light grey border to the VBox
            messageContainer.setStyle("-fx-border-color: #dcdcdc; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-padding: 5px 10px;");

            // Label for the sender name ("You" or actual username)
            Label messageUser = new Label(isUserMessage ? "You" : sender);
            messageUser.getStyleClass().add("message-user");

            // Style for the messageUser label
            messageUser.setStyle("-fx-font-weight: bold; "
                    + "-fx-padding: 5px 10px; "
                    + "-fx-margin: 2px 0; "
                    + "-fx-text-fill: #2f4f4f;");

            // Label for the actual message
            Label messageText = new Label(message);
            messageText.setWrapText(true); // Ensure text wraps properly
            messageText.getStyleClass().add("message-text");

            // Style for the messageUser label
            messageUser.setStyle("-fx-font-weight: bold; "
                    + "-fx-padding: 5px 10px; "
                    + "-fx-margin: 0 0 2px 0; "
                    + "-fx-text-fill: #2f4f4f;");

            // Apply different styles based on sender
            if (isUserMessage) {
                messageText.setStyle(messageText.getStyle() + "-fx-background-color: #ffdf00;");  // Yellow bubble for user message
            } else {
                messageText.setStyle(messageText.getStyle() + "-fx-background-color: #dcdcdc;");  // Light gray bubble for others
            }

            // Add sender label and message to the VBox
            messageContainer.getChildren().addAll(messageUser, messageText);

            // Align messages properly
            HBox chatBubble = new HBox(messageContainer);
            chatBubble.setAlignment(Pos.CENTER_LEFT); // Both aligned left

            // Add the chat bubble to the message box (VBox)
            messageBox.getChildren().add(chatBubble);
        });
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
