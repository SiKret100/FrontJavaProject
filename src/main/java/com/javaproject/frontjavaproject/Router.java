package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Router {

    public static void loadScene(Stage stage, String sceneName) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource(sceneName + "-view.fxml"));
        Scene dashboardScene = new Scene(fxmlLoader.load());

        stage.setScene(dashboardScene);
        stage.setTitle("Dashboard");
        stage.show();
    }
}
