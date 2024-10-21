package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button backButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    public boolean dataValidation(ActionEvent event) throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login == null || login.trim().isEmpty()) {
            errorLabel.setText("Login cannot be empty");
            return false;
        }

        // Regex do sprawdzenia hasła:
        // - Minimum 7 znaków
        // - Co najmniej jedna wielka litera
        // - Co najmniej jedna cyfra
        // - Co najmniej jeden znak specjalny
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$";

        if (!Pattern.matches(passwordRegex, password)) {
            errorLabel.setText("Password must contain at least 7 characters, one uppercase letter, one digit, and one special character.");
            return false;
        }

        errorLabel.setText("Login successful");
        return true;

    }

    public void handleRegister(ActionEvent event) throws IOException {
        if(!dataValidation(event)){
            return;
        }

        String login = loginField.getText();
        String password = passwordField.getText();
        String url = "http://localhost:8080/api/auth/register";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", login, password);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                errorLabel.setText("Registration successful. You can log in now!");
                //loadLoginScene(event);
            }
        } else {
            errorLabel.setText("Registration failed: " + responseCode);
        }
    }

    public void loadLoginScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "login");
    }

}
