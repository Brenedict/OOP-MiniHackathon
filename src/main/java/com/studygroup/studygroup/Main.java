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
* BINAS, There is still flaw on the logic of switching ports, IP,
* supposedly next in line is to load all scenes upon successful login
* to avoid loading them repetitively, and instead would just switch in between scenes
* however in theory this would leave the listening thread still active in the background
* */


public class Main extends Application {
    static volatile boolean finished = false;

    public static Stage stage;
    private static Scene homeScene, chatScene;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showLoginPage();
    }

    private void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sign-in.fxml"));
        Parent root = loader.load();
        Scene signinScene = new Scene(root);
        signinScene.getStylesheets().add(getClass().getResource("signin.css").toExternalForm());
        Font.loadFont(getClass().getResourceAsStream("/fonts/OpenSauce-Black.ttf"), 12);

        stage.setScene(signinScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
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
        FXMLLoader chatLoader = new FXMLLoader(Main.class.getResource("messaging2.fxml"));
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
