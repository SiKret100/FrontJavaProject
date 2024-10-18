package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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

    private final String[] regions = {"POLSKA","LUBELSKIE", "MAZOWIECKIE", "MAŁOPOLSKIE",
            "ŚLĄSKIE", "LUBUSKIE", "WIELKOPOLSKIE", "ZACHODNIOPOMORSKIE", "DOLNOŚLĄSKIE", "OPOLSKIE", "KUJAWSKO-POMORSKIE",
            "POMORSKIE", "ŁODZKIE", "ŚWIĘTOKRZYSKIE", "PODKARPACKIE", "PODLASKIE", "MAZOWIECKIE"};

    private final String[] markets = {"OBA RYNKI", "RYNEK WTÓRNY", "RYNEK PIERWOTNY"};

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

        //fieldPrive
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

    public void handleDashboardRedirect(ActionEvent event) throws IOException {
        loadDashboardScene(event);
    }

    private void loadDashboardScene(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
        Scene dashboardScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.setTitle("Add");
        stage.show();
    }

    private void AddHousingPice(ActionEvent event) throws IOException {

    }
}
