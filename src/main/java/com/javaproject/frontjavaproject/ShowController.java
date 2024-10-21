package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ShowController implements Initializable {
    @FXML
    private Button getAllButton;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button exportButton;

    @FXML
    public TableView<HousingPricesModel> tableView;

    @FXML
    private TableColumn<HousingPricesModel, String> nameColumn;

    @FXML
    private TableColumn<HousingPricesModel, String> transactionColumn;

    @FXML
    private TableColumn<HousingPricesModel, String> surfaceColumn;

    @FXML
    private TableColumn<HousingPricesModel, Integer> yearColumn;

    @FXML
    private TableColumn<HousingPricesModel, Integer> priceColumn;

    @FXML
    private ChoiceBox<String> choiceRegion;

    @FXML
    private ChoiceBox<String> choiceMarket;

    @FXML
    private ChoiceBox<String> choiceType;

    private final String[] regions = {"WSZYSTKO","POLSKA", "LUBELSKIE", "MAZOWIECKIE", "MAŁOPOLSKIE",
            "ŚLĄSKIE", "LUBUSKIE", "WIELKOPOLSKIE", "ZACHODNIOPOMORSKIE", "DOLNOŚLĄSKIE", "OPOLSKIE", "KUJAWSKO-POMORSKIE",
            "POMORSKIE", "ŁODZKIE", "ŚWIĘTOKRZYSKIE", "PODKARPACKIE", "PODLASKIE", "MAZOWIECKIE"};

    private final String[] markets = {"WSZYSTKO","RYNEK WTÓRNY", "RYNEK PIERWOTNY"};

    private final String[] types = {"WSZYSTKO","DO 40 M2", "OD 40,1 DO 60 M2", "OD 60,1 DO 80 M2", "OD 80,1 M2"};

    private JSONArray combinedArray = new JSONArray();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        transactionColumn.setCellValueFactory(new PropertyValueFactory<>("transaction"));
        surfaceColumn.setCellValueFactory(new PropertyValueFactory<>("surface"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

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

        try{
            nameColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getTableView().getItems().get(event.getTablePosition().getRow());
                housing.setName(event.getNewValue());

                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });


            transactionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            transactionColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setTransaction(event.getNewValue());
                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });

            surfaceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            surfaceColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setSurface(event.getNewValue());
                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });

            priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            priceColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setPrice(event.getNewValue());
                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });


        }
        catch(Exception e){
            AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }

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

    public void handleFetchAll() throws Exception {
        try {


            combinedArray.clear();  // to masz zerowac

            String region = choiceRegion.getValue();
            String market = choiceMarket.getValue().toLowerCase();
            String type = choiceType.getValue().toLowerCase();

            System.out.println("przed:");
            System.out.println(region+" "+market+" "+type);

            if(region.equals("WSZYSTKO") || region.equals("Choose Region")){
                region = null;
            }
            if(market.equals("wszystko") || market.equals("choose market")){
                market = null;
            }
            if(type.equals("wszystko") || type.equals("choose type")){
                type = null;
            }

            tableView.getItems().clear();

            HousingController.fetchHousing(region, market, type, combinedArray);

            List<JSONObject> jsonObjectList = new ArrayList<>();

            for (int i = 0; i < combinedArray.length(); i++) {
                jsonObjectList.add(combinedArray.getJSONObject(i));
            }

            System.out.println(jsonObjectList.size());

            jsonObjectList.sort(Comparator.comparingInt(i -> i.getInt("year")));

            jsonObjectList = jsonObjectList.reversed();

            for( JSONObject housingPrice : jsonObjectList ){
                Integer id = housingPrice.getInt("id");
                String name = housingPrice.getString("name");
                String transaction = housingPrice.getString("transaction");
                String surface = housingPrice.getString("surface");
                Integer year = housingPrice.getInt("year");
                Integer price = housingPrice.getInt("price");

                HousingPricesModel housing = new HousingPricesModel(id, name, transaction, surface, year, price);
                tableView.getItems().add(housing);
            }

        } catch (Exception e) {
            AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    public void loadDashboardScene(ActionEvent event) throws Exception {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Router.loadScene(stage, "dashboard");
    }

    public void handleDelete(ActionEvent event) throws Exception {
        HousingPricesModel selectedHousing = tableView.getSelectionModel().getSelectedItem();

        if (selectedHousing != null){
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?");
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        System.out.println(selectedHousing.getId());
                        HousingController.deleteHousingRecord(selectedHousing.getId());
                        tableView.getItems().remove(selectedHousing);
                    } catch (Exception e) {
                        AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                    }
                }
            });
        } else {
            AlertManager.showAlert(Alert.AlertType.WARNING, "Warning", "No record selected for deletion.");
        }
    }

    public void handleExport(ActionEvent event) throws Exception {
        if (combinedArray.length() == 0) {
            AlertManager.showAlert(Alert.AlertType.WARNING, "Warning", "No data available to export.");
            return;
        }

          DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save JSON File");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File jsonFile = new File(selectedDirectory, "data.json");

            try (FileWriter dataWriter = new FileWriter(jsonFile)) {
                dataWriter.write(combinedArray.toString(4));
                AlertManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Data exported successfully to " + jsonFile.getAbsolutePath());
            } catch (IOException e) {
                AlertManager.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        } else {
            AlertManager.showAlert(Alert.AlertType.WARNING, "Warning", "No directory selected. Export cancelled.");
        }
    }





}
