package com.javaproject.frontjavaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        transactionColumn.setCellValueFactory(new PropertyValueFactory<>("transaction"));
        surfaceColumn.setCellValueFactory(new PropertyValueFactory<>("surface"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        try{
            nameColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getTableView().getItems().get(event.getTablePosition().getRow());
                housing.setName(event.getNewValue());

                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });


            transactionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            transactionColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setTransaction(event.getNewValue());
                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });

            surfaceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            surfaceColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setSurface(event.getNewValue());
                try {
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });




            priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

            priceColumn.setOnEditCommit(event -> {
                HousingPricesModel housing = event.getRowValue();
                housing.setPrice(event.getNewValue());
                try{
                    HousingController.updateHousingRecord(housing);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            });
        }
        catch(Exception e){
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }

    }

    public void handleFetchAll() throws Exception {
        try {
            JSONArray combinedArray = new JSONArray();
            HousingController.fetchHousing(null, null, null, combinedArray);

            List<JSONObject> jsonObjectList = new ArrayList<>();

            for (int i = 0; i < combinedArray.length(); i++) {
                jsonObjectList.add(combinedArray.getJSONObject(i));
            }

            //System.out.println(jsonObjectList.size());

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
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }



    public void handleDashboardRedirect(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
        Scene dashboardScene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.setTitle("Dashboard");
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
                        showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No record selected for deletion.");
        }
    }





}
