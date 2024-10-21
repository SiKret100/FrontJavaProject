package com.javaproject.frontjavaproject.viewcontrollers;

import com.javaproject.frontjavaproject.authorization.AuthManager;
import com.javaproject.frontjavaproject.Router;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;


    private void handleLogin(String login, String password, ActionEvent event) throws IOException {
        String url = "http://localhost:8080/api/auth/authenticate";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", login, password);

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

                JSONObject jsonResponse = new JSONObject(response.toString());
                String token = jsonResponse.getString("token");
                AuthManager.setToken(token);
                System.out.println(AuthManager.getToken());

                errorLabel.setText("Login successful");
                loadDashboardScene(event);
            }
        } else {
            errorLabel.setText("Login failed: " + responseCode);
        }


    }

    public void userLogin(ActionEvent event) throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();
        handleLogin(login, password, event);


    }

    private void loadDashboardScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "dashboard");
    }

    public void loadRegisterScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "register");
    }
}

