package com.studygroup.studygroup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


/*
* BINAS, There is something wrong with the port and ip generator, you still need to add the leave group fucntion, a ui for homepage,
* The create new group wont close, sign up logic, pictures, file sending, MESSAGE HISTORY, PROPER CHAT UI
* */


public class Main extends Application {
    static volatile boolean finished = false;

    public static Stage stage;
    private static Scene homeScene, chatScene;

    // FIRST THING TO CHECK ON ERROR IS JDBC
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showLoginPage();
    }

    public static void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("sign-in.fxml"));

        // Check if FXML file exists
        if (Main.class.getResource("sign-in.fxml") == null) {
            throw new RuntimeException("sign-in.fxml not found!");
        }

        try {
            Parent root = loader.load();
            System.out.println("TEST"); // This should print if load is successful
            Scene signinScene = new Scene(root);
            System.out.println("Successfully loaded sign-in.fxml!");
            signinScene.getStylesheets().add(Main.class.getResource("signin.css").toExternalForm());
            Font.loadFont(Main.class.getResourceAsStream("/fonts/OpenSauce-Black.ttf"), 12);
            stage.setScene(signinScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace(); // Print stack trace to debug
        }
    }


    public static void main(String[] args) {
        launch();
    }

    public static void switchSignUpPage() throws IOException {
        try {
            // Load the sign-up page FXML
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("sign-up.fxml"));
            Parent root = loader.load();

            // Create a new scene for the sign-up page
            Scene signUpScene = new Scene(root);

            // Set the scene to the new one
            stage.setScene(signUpScene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading sign-up page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchHomePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("home-page.fxml"));
        Parent root = loader.load();
        Scene homeScene = new Scene(root);
        homeScene.getStylesheets().add(Main.class.getResource("home.css").toExternalForm());
        Font.loadFont(Main.class.getResourceAsStream("/fonts/OpenSauce-Black.ttf"), 12);

        Home homeController = loader.getController();
        homeController.loadExistingGroups(); // Explicitly load groups

        stage.setScene(homeScene);
        stage.show();
    }

    public static void switchChatPage() throws IOException {
        FXMLLoader chatLoader = new FXMLLoader(Main.class.getResource("messagingPage.fxml"));
        Parent chatRoot = chatLoader.load();
        chatScene = new Scene(chatRoot);

        stage.setScene(chatScene);
        stage.show();
    }

    @Override
    public void stop() {
        finished = true;
        System.exit(0); // Ensure all threads are terminated
    }
}
