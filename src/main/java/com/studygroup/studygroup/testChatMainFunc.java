package com.studygroup.studygroup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class testChatMainFunc extends Application {
    private ChatController chatController;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-UI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        chatController = loader.getController();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(chatController != null) {
            chatController.stop();
        }
    }
}
