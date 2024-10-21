package com.javaproject.frontjavaproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;


public class NotificationClass {

    public static void createPopup(String message, Stage stage) {

        Popup popup = new Popup();
        popup.setAutoHide(true);
        Label label = new Label(message);
        popup.getContent().add(label);
        label.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; "
                + "-fx-padding: 10px; -fx-background-radius: 10;");


        HBox hbox = new HBox(10, label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(10));
        hbox.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 10;");
        popup.getContent().add(hbox);

        popup.setOnShown(e -> {
            double x = stage.getX() + stage.getWidth() - popup.getWidth() - 20;
            double y = stage.getY() + stage.getHeight() - popup.getHeight() - 20;
            popup.setX(x);
            popup.setY(y);
        });

        popup.show(stage);


    }

}


