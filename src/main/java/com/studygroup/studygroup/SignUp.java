package com.studygroup.studygroup;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SignUp extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("sign-up.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main (String[] args) {
        launch();
    }
}
