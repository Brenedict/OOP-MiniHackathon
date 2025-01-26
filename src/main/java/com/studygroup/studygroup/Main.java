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

    private static ChatController chatController;

    public static Stage stage;

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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("chat-UI.fxml"));
        Parent root = loader.load();
        Scene homeScene = new Scene(root);

        chatController = loader.getController();

        stage.setScene(homeScene); // Set the new scene to the same primaryStage
        stage.show();


    }

    @Override
    public void stop() {

    }
}
