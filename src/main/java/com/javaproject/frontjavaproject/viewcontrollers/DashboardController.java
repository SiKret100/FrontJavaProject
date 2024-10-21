package com.javaproject.frontjavaproject.viewcontrollers;

import com.javaproject.frontjavaproject.AlertManager;
import com.javaproject.frontjavaproject.HousingController;
import com.javaproject.frontjavaproject.Router;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceRegion;

    @FXML
    private ChoiceBox<String> choiceMarket;

    @FXML
    private ChoiceBox<String> choiceType;

    @FXML
    private Button addButton;

    @FXML
    private Button showRecordsButton;

//    @FXML
//    private Label choiceRegionLabel;

    @FXML
    private Button fetchButton;

    private final String[] regions = {"POLSKA", "LUBELSKIE", "MAZOWIECKIE", "MAŁOPOLSKIE", "ŚLĄSKIE", "LUBUSKIE", "WIELKOPOLSKIE", "ZACHODNIOPOMORSKIE", "DOLNOŚLĄSKIE", "OPOLSKIE", "KUJAWSKO-POMORSKIE", "POMORSKIE", "ŁODZKIE", "ŚWIĘTOKRZYSKIE", "PODKARPACKIE", "PODLASKIE", "MAZOWIECKIE"};

    private final String[] markets = {"OBA RYNKI", "RYNEK WTÓRNY", "RYNEK PIERWOTNY"};

    private final String[] types = {"DO 40 M2", "OD 40,1 DO 60 M2", "OD 60,1 DO 80 M2", "OD 80,1 M2"};

    @FXML
    private LineChart<String, Number> lineChart;

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


    }

    public void getType(ActionEvent actionEvent) {
        String myType = choiceType.getValue().toLowerCase();
        //choiceRegionLabel.setText(myType);
    }

    public void getRegion(ActionEvent event) {
        String myRegion = choiceRegion.getValue();
        //choiceRegionLabel.setText(myRegion);
    }

    public void getMarket(ActionEvent event) {
        String myMarket = choiceMarket.getValue().toLowerCase();
        //choiceRegionLabel.setText(myMarket);
    }

    public void checkAllSelectionAndFetch(ActionEvent actionEvent) throws Exception {
        String selectedRegion = choiceRegion.getValue();
        String selectedMarket = choiceMarket.getValue();
        String selectedType = choiceType.getValue();

        if (selectedRegion == null || selectedRegion.equals("Choose Region") || selectedMarket == null || selectedMarket.equals("Choose Type") || selectedType == null || selectedType.equals("Choose Type")) {
            throw new Exception("One or more fields have default values or are empty.");
        }
    }

    public void fetchHousingPrices(ActionEvent event){
        try {

            checkAllSelectionAndFetch(event);


            String region = choiceRegion.getValue();
            String market = choiceMarket.getValue().toLowerCase(); // Pobieramy wybrany rynek
            String type = choiceType.getValue().toLowerCase();


            JSONArray combinedResponse = new JSONArray(); // Bufor do łączenia odpowiedzi z dwóch rynków

            if (market.equals("oba rynki")) {
                // 1. Zapytanie dla "rynek wtórny"
                combinedResponse = new JSONArray(HousingController.fetchHousing(region, "rynek wtórny", type, combinedResponse));

                // 2. Zapytanie dla "rynek pierwotny
                combinedResponse = new JSONArray(HousingController.fetchHousing(region, "rynek pierwotny", type, combinedResponse));

            } else {
                //POBIERANIE DANYCH DLA POJEDYNCZEGO RYNKU
                combinedResponse = new JSONArray(HousingController.fetchHousing(region, market, type, combinedResponse));
            }

            updateLineChart(combinedResponse);

        } catch (Exception e) {
            AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void updateLineChart(JSONArray combinedArray) throws Exception {
        try {
            lineChart.getData().clear();  // Czyścimy poprzednie dane na wykresie

            // Mapa do przechowywania cen dla rynku wtórnego i pierwotnego
            Map<Integer, Double> secondaryMarketPrices = new HashMap<>();
            Map<Integer, Double> primaryMarketPrices = new HashMap<>();


            //System.out.println("DATA = " + combinedArray.toString());

            for (int i = 0; i < combinedArray.length(); i++) {
                JSONObject housingPrice = combinedArray.getJSONObject(i);
                int year = housingPrice.getInt("year");
                String transaction = housingPrice.getString("transaction").toLowerCase();
                double price = housingPrice.getDouble("price");

                if (year >= 2013 && year <= 2022) {
                    if (transaction.equals("rynek wtórny")) {
                        secondaryMarketPrices.put(year, price);
                    } else if (transaction.equals("rynek pierwotny")) {
                        primaryMarketPrices.put(year, price);
                    }
                }
            }

            XYChart.Series<String, Number> secondaryMarketSeries = new XYChart.Series<>();
            secondaryMarketSeries.setName("Rynek Wtórny");

            XYChart.Series<String, Number> primaryMarketSeries = new XYChart.Series<>();
            primaryMarketSeries.setName("Rynek Pierwotny");

            for (int year = 2013; year <= 2022; year++) {
                Double secondaryPrice = secondaryMarketPrices.get(year);
                Double primaryPrice = primaryMarketPrices.get(year);

                secondaryMarketSeries.getData().add(new XYChart.Data<>(String.valueOf(year), secondaryPrice != null ? secondaryPrice : 0));
                primaryMarketSeries.getData().add(new XYChart.Data<>(String.valueOf(year), primaryPrice != null ? primaryPrice : 0));
            }

            String selectedType = choiceMarket.getValue();
            System.out.println("Wybrany typ rynku: " + selectedType);

            if (selectedType.equalsIgnoreCase("RYNEK WTÓRNY")) {
                System.out.println("Dodawanie danych dla rynku wtórnego");
                lineChart.getData().add(secondaryMarketSeries);
            } else if (selectedType.equalsIgnoreCase("RYNEK PIERWOTNY")) {
                System.out.println("Dodawanie danych dla rynku pierwotnego");
                lineChart.getData().add(primaryMarketSeries);
            } else if (selectedType.equalsIgnoreCase("OBA RYNKI")) {
                System.out.println("Dodawanie danych dla obu rynków");
                lineChart.getData().addAll(secondaryMarketSeries, primaryMarketSeries);
            }
        } catch (Exception e) {
            throw new Exception("Failed to update line chart: " + e.getMessage(), e);

        }

    }

    public void handleShowRedirect(ActionEvent event) throws IOException {
        loadShowScene(event);
    }

    private void loadShowScene(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("show-view.fxml"));
        Scene dashboardScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.setTitle("Show");
        stage.show();
    }

    public void loadAddScene(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "add");
    }


}

