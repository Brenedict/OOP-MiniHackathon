package com.studygroup.studygroup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Home extends DatabaseConnection{
    Stage stage;

    static int UserID;
    static String Username;

    public Home() throws SQLException {
        super();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void joinGroup() throws SQLException, IOException {
        sqlCommand = "INSERT INTO GroupMemberships(UserID, ChatGroupID) VALUES (?, ?);";
        statement = con.prepareStatement(sqlCommand);
        statement.setInt(1, Home.UserID);
        statement.setString(2, (String) buttonJoinGroup.getUserData());

        statement.executeUpdate();

        Main.switchHomePage();

    }


    @FXML
    public void createNewGroup() {
        try {
            // Load the FXML for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateNewGroup.fxml"));
            VBox popupRoot = loader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Create New Group");
            popupStage.setScene(new javafx.scene.Scene(popupRoot));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadChatPage() throws IOException {
        Main.switchChatPage();
    }

    @FXML
    VBox postBox;

    @FXML
    Text postTitle;

    @FXML
    Text postDescription;

    @FXML
    Button buttonJoinGroup;

    private void setPostTitle(String title) {
        postTitle.setText(title);
    }

    private void setPostDescription(String description) {
        postDescription.setText(description);
    }

    private void setButtonUserData(String userData) { buttonJoinGroup.setUserData(userData); }

    public void loadExistingGroups() {
        try {
            // Prepare the SQL query
            sqlCommand = "SELECT ChatGroupID, ChatGroupName, CreatorRole, CourseCategory, ChatTitle, ChatDescription " +
                    "FROM ChatGroups " +
                    "WHERE UserID != ? AND NOT EXISTS (" +
                    "    SELECT 1 " +
                    "    FROM GroupMemberships " +
                    "    WHERE GroupMemberships.ChatGroupID = ChatGroups.ChatGroupID " +
                    "      AND GroupMemberships.UserID = ?" +
                    ");";

            statement = con.prepareStatement(sqlCommand);
            statement.setInt(1, UserID); // Exclude groups created by the user
            statement.setInt(2, UserID); // Exclude groups where the user is already a member

            resultSet = statement.executeQuery();

            // Clear previous groups before adding new ones
            postBox.getChildren().clear();

            // Dynamically load each group component
            while (resultSet.next()) {
                String chatGroupID = resultSet.getString("ChatGroupID");
                String chatGroupName = resultSet.getString("ChatGroupName");
                String creatorRole = resultSet.getString("CreatorRole");
                String courseCategory = resultSet.getString("CourseCategory");
                String chatTitle = resultSet.getString("ChatTitle");
                String chatDescription = resultSet.getString("ChatDescription");


                // Load the FXML component for a group
                FXMLLoader loader = new FXMLLoader(getClass().getResource("group-stackable-component.fxml"));
                StackPane rectangleComponent = loader.load();

                // Set group details in the component controller
                Home componentController = loader.getController();
                componentController.setPostTitle(chatTitle);
                componentController.setPostDescription(chatDescription);
                componentController.setButtonUserData(chatGroupID); // Assign group ID to the button

                // Add the component to the VBox
                postBox.getChildren().add(rectangleComponent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("FXML Not Loaded: " + e.getMessage());
        }
    }

}
