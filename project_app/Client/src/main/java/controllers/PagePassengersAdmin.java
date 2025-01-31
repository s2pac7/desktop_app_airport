package controllers;

import runClient.Connect;
import Pojo.Passenger;
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
import java.math.BigDecimal;
import java.util.List;

import static check.Dialog.showAlert;

public class PagePassengersAdmin {

    @FXML
    private Button backToMenuButton;

    @FXML
    private TableColumn<Passenger, Integer> idPassengerColumn;

    @FXML
    private TableColumn<Passenger, String> namePassengerColumn;

    @FXML
    private TableColumn<Passenger, String> surnamePassengerColumn;

    @FXML
    private TableColumn<Passenger, String> passportNumberPassengerColumn;

    @FXML
    private TableColumn<Passenger, String> phoneNumberPassengerColumn;

    @FXML
    private TableColumn<Passenger, String> dateOfBirthPassengerColumn;

    @FXML
    private TableColumn<Passenger, BigDecimal> balancePassengerColumn;

    @FXML
    private TableColumn<Passenger, Integer> userIdPassengerColumn;

    @FXML
    private TableView<Passenger> infoTable;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button showButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;



    @FXML
    void initialize() {
        setupTableColumns();

        backToMenuButton.setOnAction(event -> backToMenu());
        showButton.setOnAction(event -> loadPassengersFromServer());
        deleteButton.setOnAction(event -> deleteSelectedPassenger());
        editButton.setOnAction(event -> openEditPassengerPage());
        searchButton.setOnAction(event -> searchPassengerBySurname());
    }

    private void setupTableColumns() {
        idPassengerColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdPassengerColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        namePassengerColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnamePassengerColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        passportNumberPassengerColumn.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        dateOfBirthPassengerColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        phoneNumberPassengerColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        balancePassengerColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        searchButton.setOnAction(event -> searchPassengerBySurname());
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

    private void loadPassengersFromServer() {
        try {
            infoTable.getItems().clear();

            Connect.client.sendMessage("getAllPassengers");
            String response = (String) Connect.client.readObject();

            if ("OK".equals(response)) {
                List<Passenger> passengers = (List<Passenger>) Connect.client.readObject();

                Platform.runLater(() -> {
                    ObservableList<Passenger> passengerData = FXCollections.observableArrayList(passengers);
                    infoTable.setItems(passengerData);
                });
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить пассажиров: " + e.getMessage());
        }
    }


    private void searchPassengerBySurname() {
        String searchQuery = searchField.getText().trim();

        if (searchQuery.isEmpty()) {
            showAlert("Предупреждение", "Введите фамилию для поиска.");
            return;
        }
        try {
            Connect.client.sendMessage("searchPassengerBySurname");
            Connect.client.sendMessage(searchQuery);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                List<Passenger> passengers = (List<Passenger>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Passenger> passengerData = FXCollections.observableArrayList(passengers);
                    infoTable.setItems(passengerData);
                });
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось найти пассажира.");
        }
    }

    private void deleteSelectedPassenger() {
        Passenger selectedPassenger = infoTable.getSelectionModel().getSelectedItem();
        if (selectedPassenger != null) {
            try {
                Connect.client.sendMessage("deletePassenger");
                Connect.client.sendMessage(String.valueOf(selectedPassenger.getId()));

                String response = (String) Connect.client.readObject();
                if ("OK".equals(response)) {
                    showAlert("Успех", "Пассажир удален.");
                    loadPassengersFromServer();
                } else {
                    showAlert("Ошибка", response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось удалить пассажира.");
            }
        } else {
            showAlert("Предупреждение", "Пожалуйста, выберите пассажира для удаления.");
        }
    }

    private void openEditPassengerPage() {
        Passenger selectedPassenger = infoTable.getSelectionModel().getSelectedItem();

        if (selectedPassenger != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/editPassenger.fxml"));
                Parent root = loader.load();

                EditPassengerController editController = loader.getController();
                editController.setPassengerData(selectedPassenger);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.setOnHiding(event -> loadPassengersFromServer());

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Ни один пассажир не выбран для редактирования");
        }
    }
}
