package com.studygroup.studygroup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application{
    static volatile boolean finished = false;
    Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showLoginPage(stage);
    }

    private void showLoginPage(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root);

        Login loginController = loader.getController();
        loginController.setStage(primaryStage);  // Pass the primaryStage to the Login controller

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void switchHomePage(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Parent root = loader.load();
        Scene homeScene = new Scene(root);


        primaryStage.setScene(homeScene); // Set the new scene to the same primaryStage
        primaryStage.show();
        Home homeController = loader.getController();
        homeController.loadExistingGroups();

    }
}
