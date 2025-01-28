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


/*
* BINAS, There is still flaw on the logic of switching ports, IP,
* supposedly next in line is to load all scenes upon successful login
* to avoid loading them repetitively, and instead would just switch in between scenes
* however in theory this would leave the listening thread still active in the background
* */


public class Main extends Application{
    static volatile boolean finished = false;

    private static ChatController chatController;

    public static Stage stage;
    private static Scene homeScene, chatScene;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showLoginPage();
    }

    private void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root);

        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void successfulLogin() throws IOException {
        FXMLLoader loader;
        Parent root;

        // Immediately load all scenes
        loader = FXMLLoader.load(Main.class.getResource("home.fxml"));
        root = loader.load();
        homeScene = new Scene(root);
    }

    public static void switchHomePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Parent root = loader.load();
        Scene homeScene = new Scene(root);

        Home homeController = loader.getController();
        homeController.loadExistingGroups();

        stage.setScene(homeScene); // Set the new scene to the same primaryStage
        stage.show();


    }

    public static void switchChatPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("messaging2.fxml"));
        Parent root = loader.load();
        Scene homeScene = new Scene(root);

        chatController = loader.getController();

        stage.setScene(homeScene); // Set the new scene to the same primaryStage
        stage.show();


    }

    // Use this to kill the bg threads
    @Override
    public void stop() {

    }
}
