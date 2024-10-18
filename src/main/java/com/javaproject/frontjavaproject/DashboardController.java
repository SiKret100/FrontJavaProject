package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

//    @FXML
//    private Label choiceRegionLabel;

    @FXML
    private Button fetchButton;

    private final String[] regions = {"POLSKA","LUBELSKIE", "MAZOWIECKIE", "MAŁOPOLSKIE",
    "ŚLĄSKIE", "LUBUSKIE", "WIELKOPOLSKIE", "ZACHODNIOPOMORSKIE", "DOLNOŚLĄSKIE", "OPOLSKIE", "KUJAWSKO-POMORSKIE",
    "POMORSKIE", "ŁODZKIE", "ŚWIĘTOKRZYSKIE", "PODKARPACKIE", "PODLASKIE", "MAZOWIECKIE"};

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

    //barChart section

    public void checkAllSelectionAndFetch(ActionEvent actionEvent) {
        String selectedRegion = choiceRegion.getValue();
        String selectedMarket = choiceMarket.getValue();
        String selectedType = choiceType.getValue();

        if( selectedRegion != null && !selectedRegion.equals("Choose Region") &&
            selectedMarket != null && !selectedMarket.equals("Choose Type") &&
            selectedType != null && !selectedType.equals("Choose Type")){
            fetchHousingPrices(actionEvent);
        }
    }

    public void fetchHousingPrices(ActionEvent event) {
        try {
            String region = URLEncoder.encode(choiceRegion.getValue(), StandardCharsets.UTF_8.toString());
            String market = choiceMarket.getValue().toLowerCase(); // Pobieramy wybrany rynek
            String type = URLEncoder.encode(choiceType.getValue().toLowerCase(), StandardCharsets.UTF_8.toString());

            JSONArray combinedResponse = new JSONArray(); // Bufor do łączenia odpowiedzi z dwóch rynków

            if (market.equals("oba rynki")) {
                // 1. Zapytanie dla "rynek wtórny"
                String secondaryMarketUrl = String.format(
                        "http://localhost:8080/api/housingPrices/?name=%s&transaction=%s&surface=%s",
                        region, URLEncoder.encode("rynek wtórny", StandardCharsets.UTF_8.toString()), type);

                URL secondaryUrl = new URL(secondaryMarketUrl);
                HttpURLConnection secondaryConnection = (HttpURLConnection) secondaryUrl.openConnection();
                secondaryConnection.setRequestMethod("GET");
                secondaryConnection.setRequestProperty("Content-Type", "application/json");

                String token = AuthManager.getToken();
                if (token != null && !token.isEmpty()) {
                    secondaryConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                int secondaryResponseCode = secondaryConnection.getResponseCode();
                if (secondaryResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(secondaryConnection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    in.close();

                    // Przekształcamy odpowiedź na JSONArray i dodajemy do combinedResponse
                    JSONArray secondaryResponse = new JSONArray(responseBuilder.toString());
                    //Dodajemy do combinedArray obikety z pojedynczego connection
                    for (int i = 0; i < secondaryResponse.length(); i++) {
                        combinedResponse.put(secondaryResponse.getJSONObject(i));
                    }
                } else {
                    System.out.println("GET request failed for secondary market: " + secondaryResponseCode);
                }

//                System.out.println("1 - fetch");
//                System.out.println(combinedResponse.toString());

                // 2. Zapytanie dla "rynek pierwotny"
                String primaryMarketUrl = String.format(
                        "http://localhost:8080/api/housingPrices/?name=%s&transaction=%s&surface=%s",
                        region, URLEncoder.encode("rynek pierwotny", StandardCharsets.UTF_8.toString()), type);

                URL primaryUrl = new URL(primaryMarketUrl);
                HttpURLConnection primaryConnection = (HttpURLConnection) primaryUrl.openConnection();
                primaryConnection.setRequestMethod("GET");
                primaryConnection.setRequestProperty("Content-Type", "application/json");

                if (token != null && !token.isEmpty()) {
                    primaryConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                int primaryResponseCode = primaryConnection.getResponseCode();
                if (primaryResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(primaryConnection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    in.close();

                    // Przekształcamy odpowiedź na JSONArray i dodajemy do combinedResponse
                    JSONArray primaryResponse = new JSONArray(responseBuilder.toString());

                    for (int i = 0; i < primaryResponse.length(); i++) {
                        combinedResponse.put(primaryResponse.getJSONObject(i));
                    }

//                    System.out.println("2 - fetch");
//                    System.out.println(combinedResponse.toString());
                } else {
                    System.out.println("GET request failed for primary market: " + primaryResponseCode);
                }
            } else {
                // Zapytanie dla pojedynczego rynku
                String apiUrl = String.format(
                        "http://localhost:8080/api/housingPrices/?name=%s&transaction=%s&surface=%s",
                        region, URLEncoder.encode(market, StandardCharsets.UTF_8.toString()), type);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                String token = AuthManager.getToken();
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }

                    JSONArray singleResponse = new JSONArray(responseBuilder.toString());
                    for (int i = 0; i < singleResponse.length(); i++) {
                        combinedResponse.put(singleResponse.getJSONObject(i));
                    }

//                    System.out.println("SINGLE FETCH");
                    in.close();
                } else {
                    System.out.println("GET request failed: " + responseCode);
                }
            }

//            System.out.println("FETCH PRZED UPDATE: " + combinedResponse.toString());
            updateLineChart(combinedResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLineChart(JSONArray combinedArray) {
        lineChart.getData().clear();  // Czyścimy poprzednie dane na wykresie

        // Mapa do przechowywania cen dla rynku wtórnego i pierwotnego
        Map<Integer, Double> secondaryMarketPrices = new HashMap<>();
        Map<Integer, Double> primaryMarketPrices = new HashMap<>();


        System.out.println("DATA = " + combinedArray.toString());

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

            secondaryMarketSeries.getData().add(new XYChart.Data<>(String.valueOf(year),
                    secondaryPrice != null ? secondaryPrice : 0));
            primaryMarketSeries.getData().add(new XYChart.Data<>(String.valueOf(year),
                    primaryPrice != null ? primaryPrice : 0));
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
    }

    public void handleAddRedirect(ActionEvent event) throws IOException {
        loadAddScene(event);
    }

    private void loadAddScene(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-view.fxml"));
        Scene dashboardScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.setTitle("Add");
        stage.show();
    }

}

