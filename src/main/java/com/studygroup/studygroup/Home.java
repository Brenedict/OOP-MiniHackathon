package com.studygroup.studygroup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    public void joinGroup() {

    }

    @FXML
    public void createNewGroup() {

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

    private void setPostTitle(String title) {
        postTitle.setText(title);
    }

    private void setPostDescription(String description) {
        postDescription.setText(description);
    }

    public void loadExistingGroups() {
        String chatGroupID, chatGroupName, creatorRole, courseCategory, chatTitle, chatDescription;
        try {
            sqlCommand = "SELECT * FROM ChatGroups WHERE UserID != ?";
            statement = con.prepareStatement(sqlCommand);
            statement.setInt(1, UserID);

            resultSet = statement.executeQuery();

            while(resultSet.next()) {

                chatGroupID = resultSet.getString(1);
                chatGroupName = resultSet.getString(2);
                creatorRole = resultSet.getString(3);
                courseCategory = resultSet.getString(4);
                chatTitle = resultSet.getString(5);
                chatDescription = resultSet.getString(6);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("group-stackable-component.fxml"));
                StackPane rectangleComponent = loader.load();

                Home componentController = loader.getController();
                componentController.setPostTitle(chatTitle);
                componentController.setPostDescription(chatDescription);

                postBox.getChildren().add(rectangleComponent);
            }
        }
        catch (SQLException e) {
           e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("FXML Not loaded");
        }
    }

}
