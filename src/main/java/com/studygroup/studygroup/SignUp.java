package com.studygroup.studygroup;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUp extends DatabaseConnection {
    public SignUp() throws SQLException {
        super();
    }

    @FXML
    TextField firstnameField;

    @FXML
    TextField lastnameField;

    @FXML
    TextField usernameField;

    @FXML
    TextField emailField;

    @FXML
    TextField passwordField;

    @FXML
    TextField phonenumberField;

    @FXML
    Button setMale;

    @FXML
    Button setFemale;

    @FXML
    Button setNull;

    @FXML
    RadioButton setTutor;

    @FXML
    RadioButton setStudent;

    String gender;
    String role;
    @FXML
    public void createAccount() throws Exception {
        long userID;

        // Initialize the PreparedStatement for checking existing UserID
        String checkUserIdSql = "SELECT UserID FROM credentials WHERE UserID = ?";
        statement = con.prepareStatement(checkUserIdSql);

        do {
            // Generate a random UserID between 10000 and 999999999
            userID = (long) (Math.random() * (999999999 - 10000 + 1) + 10000);

            // Check if the generated UserID already exists in the database
            statement.setLong(1, userID);
            ResultSet resultSet = statement.executeQuery();

            // If resultSet has any rows, the UserID already exists
            if (resultSet.next()) {
                // UserID exists, so regenerate
                continue;
            } else {
                // UserID is unique, break out of the loop
                break;
            }
        } while (true); // Keep trying until a unique UserID is found

        // SQL command to insert user data
        String sqlCommand = "INSERT INTO credentials (UserID, FirstName, LastName, Username, EmailAddress, Password, Gender, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        // Prepare the statement with the connection
        statement = con.prepareStatement(sqlCommand);

        // Set the values from the TextFields and PasswordField
        statement.setLong(1, userID);                      // Generated UserID
        statement.setString(2, firstnameField.getText());   // First name from TextField
        statement.setString(3, lastnameField.getText());    // Last name from TextField
        statement.setString(4, usernameField.getText());    // Username from TextField
        statement.setString(5, emailField.getText());       // Email address from TextField
        statement.setString(6, passwordField.getText());    // Password from PasswordField
        statement.setString(7, gender);                     // Gender, assuming it's set from elsewhere
        statement.setString(8, role);                     // Role (you can change this to reflect the actual role)

        // Execute the update (insert data into the table)
        statement.executeUpdate();

        Main.showLoginPage();
    }

    @FXML
    public void setGender(ActionEvent e) {
        if(e.getSource() == setMale) {
            gender = "Male";
        }
        else if(e.getSource() == setFemale) {
            gender = "Female";
        } else {
            gender = null;
        }
    }

    @FXML
    public void setRole(ActionEvent e) {
        if(e.getSource() == setTutor) {
            role = "Tutor";
        }
        else if(e.getSource() == setFemale) {
            role = "Student";
        }
    }


    @FXML
    public void cancelSignUp() throws Exception {
        Main.showLoginPage();
    }
}
