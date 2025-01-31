package controllers;

import Patterns.Observer.TableLogger;
import Patterns.Observer.TableObserver;
import Pojo.Aircraft;
import Pojo.Flight;
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
import runClient.Connect;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static check.Dialog.showAlert;

public class OnlineTableController {

    @FXML
    private TableColumn<Flight, String> arrivalAirportColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> arrivalTimeColumn;

    @FXML
    private Button backToMenuButton;

    @FXML
    private TableColumn<Flight, String> departureAirportColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> departureTimeColumn;

    @FXML
    private TableColumn<Flight, String> flightNumberColumn;

    @FXML
    private TableView<Flight> infoTable;

    @FXML
    private TableColumn<Aircraft, String> nameAircraftColumn;

    @FXML
    private Button updateTable;

    private final List<TableObserver> observers = new ArrayList<>(); // Список наблюдателей
    private final ObservableList<Flight> flightData = FXCollections.observableArrayList(); // Данные таблицы

    @FXML
    void initialize() {
        setupTableColumns();
        infoTable.setItems(flightData);

        TableLogger logger = new TableLogger(); // Создаем наблюдателя
        addObserver(logger);                    // Регистрируем его

        updateTable.setOnAction(event -> {
            loadOnlineTableData(); // Загружаем данные
            notifyObservers();     // Уведомляем наблюдателей
        });

        backToMenuButton.setOnAction(event -> backToMenu());
    }

    private void setupTableColumns() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        departureAirportColumn.setCellValueFactory(new PropertyValueFactory<>("departureAirport"));
        arrivalAirportColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalAirport"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        nameAircraftColumn.setCellValueFactory(new PropertyValueFactory<>("nameAircraft"));
    }

    private void loadOnlineTableData() {
        try {
            Connect.client.sendMessage("getOnlineTableData");
            String response = (String) Connect.client.readObject();

            if ("OK".equals(response)) {
                List<Flight> flights = (List<Flight>) Connect.client.readObject();
                Platform.runLater(() -> {
                    flightData.setAll(flights);
                });
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные таблицы: " + e.getMessage());
        }
    }

    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addObserver(TableObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TableObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (TableObserver observer : observers) {
            observer.update(flightData);
        }
    }
}
