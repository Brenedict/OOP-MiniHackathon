package com.studygroup.studygroup;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Login extends DatabaseConnection {

    public Login() throws SQLException{
        // Establish sql connection
        super();
    }

   @FXML
   TextField usernameField;

   @FXML
   PasswordField passwordField;

   @FXML
   public void handleSignUpLinkClick() throws IOException {
       Main.switchSignUpPage();
   }

   @FXML
   public void loginAccount() throws SQLException {

       String username = usernameField.getText();
       String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Empty Fields");
            a.show();
        }

        System.out.println("Login Pressed");

        sqlCommand = "SELECT UserID, Username FROM Credentials WHERE EmailAddress = ? AND Password = ?";
        statement = con.prepareStatement(sqlCommand);
        statement.setString(1, username);
        statement.setString(2, password);

        resultSet = statement.executeQuery();

        try {
            if (resultSet.next()) {
                Main.stage.close();
                Home.UserID = resultSet.getInt(1);
                Home.Username = resultSet.getString(2);
                System.out.println(Home.UserID);
                Main.switchHomePage();
            }
        }
        catch (SQLException e) {
            // Add function to indicate login details is wrong
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}


