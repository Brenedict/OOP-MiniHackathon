package com.studygroup.studygroup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.sql.SQLException;

public class Profile extends DatabaseConnection{
    public Profile() throws SQLException {
        super();
    }


    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailaddressField;
    @FXML
    private TextField phonenumberField;
    @FXML
    private PasswordField currentpasswordField;
    @FXML
    private PasswordField newpasswordField;

    @FXML
    private void saveChanges(ActionEvent e) throws Exception {
        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String username = usernameField.getText();
        String email = emailaddressField.getText();
        String phone = phonenumberField.getText();
        String currentPassword = currentpasswordField.getText();
        String newPassword = newpasswordField.getText();

        try{
            // Create SQL update query
            String updateQuery = "UPDATE credentials SET first_name = ?, last_name = ?, email = ?, phone = ?, password = ? WHERE UserID = ?";

            statement = con.prepareStatement(updateQuery);
            // Set values in the PreparedStatement
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phone);

            // Only update password if it's not empty
            if (!newPassword.isEmpty()) {
                statement.setString(5, newPassword);  // Updating password
            } else {
                statement.setString(5, currentPassword);  // Keep current password if no new one
            }

            statement.setInt(6, Home.UserID);

            // Execute the update query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User profile updated successfully.");
            } else {
                System.out.println("No user found with that username.");
            }

        } catch (SQLException error) {
            error.printStackTrace();
            // Handle any errors that occur during database connection or query execution
        }
        Main.switchHomePage();
    }

    @FXML
    private void cancelChanges(ActionEvent e) throws IOException {
        Main.switchHomePage();
    }

    @FXML
    private void deleteAccount(ActionEvent e) throws Exception {
        // Handle deleting the account (show confirmation and proceed with deletion)
        Main.showLoginPage();
    }

    @FXML
    private void setGender(ActionEvent e) {

    }


}
