package com.studygroup.studygroup;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChatController {

    @FXML
    private TextArea messageArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private VBox sidePanel;

    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private String name;
    private volatile boolean running = true; // Used to control the ReadThread loop

    private String[][] groupDetails = {
            {"239.0.0.0", "1000"},
            {"239.0.0.1", "1001"}
    };

    public void initialize() {
        // Initialize the default group
        initChat("239.0.0.0", 1000);

        // Add buttons for group switching
        for (String[] groupDetail : groupDetails) {
            String groupAddress = groupDetail[0];
            int groupPort = Integer.parseInt(groupDetail[1]);
            Button button = new Button("Connect to " + groupAddress + ":" + groupPort);
            button.setOnAction(e -> switchGroup(groupAddress, groupPort));
            sidePanel.getChildren().add(button);
        }

        // Set actions for sending messages
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());
    }

    private void initChat(String host, int port) {
        try {
            if (socket != null) {
                socket.leaveGroup(group); // Leave the previous group if switching
                socket.close();
            }

            group = InetAddress.getByName(host);
            this.port = port;

            // Ask for the user's name
            TextInputDialog dialog = new TextInputDialog();
            dialog.setContentText("Enter your name:");
            name = dialog.showAndWait().orElse("User");

            // Initialize the socket
            socket = new MulticastSocket(this.port);
            socket.setTimeToLive(1); // Local network
            socket.joinGroup(group);

            // Start the message-reading thread
            running = true;
            new Thread(new ReadThread(socket, group, this.port)).start();

            // Notify the user
            appendMessage("Connected to group " + host + ":" + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                String fullMessage = name + ": " + message;
                messageArea.appendText(fullMessage + "\n");

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
        appendMessage("Switching to group " + groupAddress + ":" + port);
        initChat(groupAddress, port);
    }

    private void appendMessage(String message) {
        Platform.runLater(() -> messageArea.appendText(message + "\n"));
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
                        appendMessage(message);
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
