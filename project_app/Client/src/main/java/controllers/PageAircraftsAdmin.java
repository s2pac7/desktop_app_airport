package controllers;

import runClient.Connect;
import Pojo.Aircraft;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;

public class PageAircraftsAdmin {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Aircraft, String> aircraftsNameColumn;

    @FXML
    private Button backToMenuButton;

    @FXML
    private TableColumn<Aircraft, String> aircraftsTypeColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Aircraft, Integer> idAircfaftsColumn;

    @FXML
    private TableView<Aircraft> infoTable;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button showButton;

    @FXML
    private Button editButton;

    @FXML
    void initialize() {
        setupTableColumns();

        backToMenuButton.setOnAction(event -> backToMenu());
        addButton.setOnAction(event -> openAddAircraftPage());
        showButton.setOnAction(event -> loadAircraftsFromServer());
        deleteButton.setOnAction(event -> deleteSelectedAircraft());
        editButton.setOnAction(event -> openEditAircraftPage());

    }

    private void setupTableColumns() {
        idAircfaftsColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        aircraftsNameColumn.setCellValueFactory(new PropertyValueFactory<>("aircraftName"));
        aircraftsTypeColumn.setCellValueFactory(new PropertyValueFactory<>("aircraftType"));
        searchButton.setOnAction(event -> searchAircraftByName());

    }

    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddAircraftPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addAircraft.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setOnHiding(event -> loadAircraftsFromServer());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditAircraftPage() {
        Aircraft selectedAircraft = infoTable.getSelectionModel().getSelectedItem();

        if (selectedAircraft != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/editAircraft.fxml"));
                Parent root = loader.load();

                EditAircraftController editController = loader.getController();
                editController.setAircraftData(selectedAircraft);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.setOnHiding(event -> loadAircraftsFromServer());

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Ни один самолет не выбран для редактирования");
        }
    }

    private void loadAircraftsFromServer() {
        infoTable.getItems().clear();

        Connect.client.sendMessage("getAllAircrafts");
        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            try {
                List<Aircraft> aircrafts = (List<Aircraft>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Aircraft> aircraftData = FXCollections.observableArrayList(aircrafts);
                    infoTable.setItems(aircraftData);
                });
            } catch (ClassCastException e) {
                showAlert("Error", "Не удалось загрузить самолеты: неверный ответ от сервера");
            }
        } else {
            showAlert("Error", response);
        }
    }

    private void searchAircraftByName() {
        String searchQuery = searchField.getText().trim();

        if (searchQuery.isEmpty()) {
            showAlert("Предупреждение", "Пожалуйста, введите имя для поиска.");
            return;
        }

        Connect.client.sendMessage("searchAircraftByName");
        Connect.client.sendMessage(searchQuery);

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            try {
                List<Aircraft> aircrafts = (List<Aircraft>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Aircraft> aircraftData = FXCollections.observableArrayList(aircrafts);
                    infoTable.setItems(aircraftData);
                });
            } catch (ClassCastException e) {
                showAlert("Error", "Не удалось найти самолеты: неверный ответ от сервера");
            }
        } else {
            showAlert("Error", response);
        }
    }

    private void deleteSelectedAircraft() {
        Aircraft selectedAircraft = infoTable.getSelectionModel().getSelectedItem();
        if (selectedAircraft != null) {
            Connect.client.sendMessage("deleteAircraft");
            Connect.client.sendMessage(String.valueOf(selectedAircraft.getId()));
            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Самолет успешно удален");
                loadAircraftsFromServer();
            } else {
                showAlert("Error", response);
            }
        } else {
            showAlert("Error", "Ни один самолет не выбран");
        }
    }
}
