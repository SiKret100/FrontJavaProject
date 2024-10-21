package com.javaproject.frontjavaproject;

import javafx.scene.control.Alert;

public class AlertManager {

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
