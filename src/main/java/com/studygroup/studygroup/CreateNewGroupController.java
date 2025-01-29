package com.studygroup.studygroup;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class CreateNewGroupController extends DatabaseConnection {

    @FXML
    private TextField groupNameField;

    @FXML
    private TextField courseCategoryField;

    @FXML
    private TextField chatTitleField;

    @FXML
    private TextArea chatDescriptionField;

    private Stage stage; // Reference to the popup stage

    public CreateNewGroupController() throws SQLException {
        super();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        // Add a close request handler to handle "X" button click
        this.stage.setOnCloseRequest(event -> {
            closeWindow(); // Call the closeWindow method when the user clicks the "X" button
        });
    }

    @FXML
    private void closeWindow() {
        if (stage != null) {
            stage.close(); // Close the window
        }
    }

    @FXML
    private void createGroup() {
        String groupName = groupNameField.getText().trim();
        String courseCategory = courseCategoryField.getText().trim();
        String chatTitle = chatTitleField.getText().trim();
        String chatDescription = chatDescriptionField.getText().trim();

        if (groupName.isEmpty() || courseCategory.isEmpty() || chatTitle.isEmpty() || chatDescription.isEmpty()) {
            System.out.println("Please fill in all fields."); // Add proper user feedback here
            return;
        }

        try {
            // Generate unique IP and Port
            String ipAddress;
            int portNumber;
            do {
                ipAddress = generateRandomMulticastIP();
                portNumber = generateRandomPort();
            } while (isIPAndPortInUse(ipAddress, portNumber)); // Ensure IP and port are unique

            // Insert into the ChatGroups table
            String sqlCommand = "INSERT INTO ChatGroups (ChatGroupID, ChatGroupName, CreatorRole, CourseCategory, ChatTitle, ChatDescription, UserID, IP, PortNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = con.prepareStatement(sqlCommand);

            String chatGroupID = generateChatGroupID(); // Generate unique ChatGroupID

            statement.setString(1, chatGroupID);
            statement.setString(2, groupName);
            statement.setString(3, "tutor"); // Assuming the creator is always a "tutor"
            statement.setString(4, courseCategory);
            statement.setString(5, chatTitle);
            statement.setString(6, chatDescription);
            statement.setInt(7, Home.UserID); // Set the current user's ID
            statement.setString(8, ipAddress);
            statement.setInt(9, portNumber);

            statement.executeUpdate();

            // Insert creator into the GroupMemberships table
            String groupMembershipQuery = "INSERT INTO GroupMemberships (UserID, ChatGroupID) VALUES (?, ?)";
            statement = con.prepareStatement(groupMembershipQuery);
            statement.setInt(1, Home.UserID); // Add the creator's UserID
            statement.setString(2, chatGroupID); // Link to the newly created ChatGroupID
            statement.executeUpdate();

            System.out.println("Group created successfully, and creator added to the group!");
            closeWindow(); // Close the pop-up after successful creation

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomMulticastIP() {
        Random random = new Random();
        int firstOctet = 224 + random.nextInt(16); // Ensures it's between 224 and 239
        int secondOctet = random.nextInt(256);
        int thirdOctet = random.nextInt(256);
        int fourthOctet = random.nextInt(256);

        return firstOctet + "." + secondOctet + "." + thirdOctet + "." + fourthOctet;
    }

    private int generateRandomPort() {
        Random random = new Random();
        return 1024 + random.nextInt(49151 - 1024 + 1); // Generates a port between 1024 and 49151
    }

    private boolean isIPAndPortInUse(String ip, int port) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM ChatGroups WHERE IP = ? AND PortNumber = ?";
        statement = con.prepareStatement(checkQuery);
        statement.setString(1, ip);
        statement.setInt(2, port);

        resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; // Returns true if IP and Port already exist
        }
        return false;
    }

    private String generateChatGroupID() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char) ('A' + random.nextInt(26));
            idBuilder.append(c);
        }
        return idBuilder.toString();
    }
}
