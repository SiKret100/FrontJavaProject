package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class AddController implements Initializable {

    @FXML
    private Label testLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private ChoiceBox<String> choiceRegion;

    @FXML
    private ChoiceBox<String> choiceMarket;

    @FXML
    private ChoiceBox<String> choiceType;

    @FXML
    private TextField fieldPrice;

    @FXML
    private Button testButton;

    private final String[] regions = {"POLSKA", "LUBELSKIE", "MAZOWIECKIE", "MAŁOPOLSKIE",
            "ŚLĄSKIE", "LUBUSKIE", "WIELKOPOLSKIE", "ZACHODNIOPOMORSKIE", "DOLNOŚLĄSKIE", "OPOLSKIE", "KUJAWSKO-POMORSKIE",
            "POMORSKIE", "ŁODZKIE", "ŚWIĘTOKRZYSKIE", "PODKARPACKIE", "PODLASKIE", "MAZOWIECKIE"};

    private final String[] markets = {"RYNEK WTÓRNY", "RYNEK PIERWOTNY"};

    private final String[] types = {"DO 40 M2", "OD 40,1 DO 60 M2", "OD 60,1 DO 80 M2", "OD 80,1 M2"};

    @Override //inicjalizacja danych sceny przy ładowaniu
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //choiceRegion section
        choiceRegion.getItems().addAll(regions);
        choiceRegion.setOnAction(this::getRegion);//refernecja do metody getRegion
        choiceRegion.setValue("Choose Region");

        //choiceMarket
        choiceMarket.getItems().addAll(markets);
        choiceMarket.setOnAction(this::getMarket);
        choiceMarket.setValue("Choose Market");

        //choiceType
        choiceType.getItems().addAll(types);
        choiceType.setOnAction(this::getType);
        choiceType.setValue("Choose Type");

        //fieldPrice
        fieldPrice.setPromptText("Set price");

    }

    public void getType(ActionEvent actionEvent) {
        String myType = choiceType.getValue().toLowerCase();
    }

    public void getRegion(ActionEvent event) {
        String myRegion = choiceRegion.getValue();
    }

    public void getMarket(ActionEvent event) {
        String myMarket = choiceMarket.getValue().toLowerCase();
    }

    public void loadDashboardScene(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "dashboard");
    }

    public void addHousingPrice(ActionEvent event) {
        String region = choiceRegion.getValue();
        String market = choiceMarket.getValue().toLowerCase();
        String type = choiceType.getValue().toLowerCase();
        String price = fieldPrice.getText();

        try {
            HousingController.addHousingPrice(region, market, type, price);
            AlertManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Housing price added successfully.");
            NotificationClass.createPopup("Dodano",   (Stage) ((Node) event.getSource()).getScene().getWindow());

            choiceRegion.setValue("Choose Region");
            choiceMarket.setValue("Choose Market");
            choiceType.setValue("Choose Type");
            fieldPrice.clear();
            fieldPrice.setPromptText("Set price");



        } catch (Exception e) {
            AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

}
