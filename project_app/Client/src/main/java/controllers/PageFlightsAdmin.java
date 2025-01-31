package controllers;

import runClient.Connect;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static check.Dialog.showAlert;

public class PageFlightsAdmin {

    @FXML
    private Button backToMenuButton;

    @FXML
    private TableColumn<Flight, Integer> idFlightColumn;

    @FXML
    private TableColumn<Flight, String> flightNumberColumn;

    @FXML
    private TableColumn<Flight, String> departureAirportColumn;

    @FXML
    private TableColumn<Flight, String> arrivalAirportColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> departureTimeColumn;

    @FXML
    private TableColumn<Flight, LocalDateTime> arrivalTimeColumn;

    @FXML
    private TableColumn<Flight, String> nameAircraftColumn;

    @FXML
    private TableView<Flight> infoTable;

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
    private Button addButton;

    @FXML
    private Button createReportButton;

    @FXML
    void initialize() {
        setupTableColumns();

        backToMenuButton.setOnAction(event -> backToMenu());
        showButton.setOnAction(event -> loadFlightsFromServer());
        deleteButton.setOnAction(event -> deleteSelectedFlight());
        searchButton.setOnAction(event -> searchFlightByNumber());
        addButton.setOnAction(event -> openAddFlightPage());
        editButton.setOnAction(event -> openEditFlightPage());
        createReportButton.setOnAction(event -> generateReport());
    }

    private void setupTableColumns() {
        idFlightColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        departureAirportColumn.setCellValueFactory(new PropertyValueFactory<>("departureAirport"));
        arrivalAirportColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalAirport"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        nameAircraftColumn.setCellValueFactory(new PropertyValueFactory<>("nameAircraft"));
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

    private void openEditFlightPage() {
        Flight selectedFlight = infoTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/editFlight.fxml"));
                Parent root = loader.load();

                EditFlightController editController = loader.getController();
                editController.setFlightData(selectedFlight);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.setOnHiding(event -> loadFlightsFromServer());

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Ни один рейс не выбран для редактирования");
        }
    }

    private void loadFlightsFromServer() {
        try {
            infoTable.getItems().clear();

            Connect.client.sendMessage("getAllFlights");

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                List<Flight> flights = (List<Flight>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Flight> flightData = FXCollections.observableArrayList(flights);
                    infoTable.setItems(flightData);
                });
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить рейсы: " + e.getMessage());
        }
    }

    private void searchFlightByNumber() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            showAlert("Предупреждение", "Введите номер рейса для поиска.");
            return;
        }

        try {
            Connect.client.sendMessage("searchFlightByNumber");
            Connect.client.sendMessage(searchQuery);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                List<Flight> flights = (List<Flight>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Flight> flightData = FXCollections.observableArrayList(flights);
                    infoTable.setItems(flightData);
                });
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось найти рейс.");
        }
    }

    private void deleteSelectedFlight() {
        Flight selectedFlight = infoTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                Connect.client.sendMessage("deleteFlight");
                Connect.client.sendMessage(String.valueOf(selectedFlight.getId()));

                String response = (String) Connect.client.readObject();
                if ("OK".equals(response)) {
                    showAlert("Успех", "Рейс удален.");
                    loadFlightsFromServer();
                } else {
                    showAlert("Ошибка", response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось удалить рейс.");
            }
        } else {
            showAlert("Предупреждение", "Пожалуйста, выберите рейс для удаления.");
        }
    }

    private void generateReport() {
        try {
            String fileName = "report.txt";
            Connect.client.sendMessage("generateReport");
            Connect.client.sendMessage(fileName);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                byte[] fileBytes = (byte[]) Connect.client.readObject();

                // Относительный путь для сохранения отчета
                String reportsDir = System.getProperty("user.dir") + File.separator + "Reports";
                Path reportPath = Paths.get(reportsDir, fileName);
                Files.createDirectories(reportPath.getParent()); // Убедиться, что папка существует
                Files.write(reportPath, fileBytes);

                showAlert("Успех", "Отчет сохранен: " + reportPath.toAbsolutePath());
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось создать отчет.");
        }
    }


    private void openAddFlightPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addFlight.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Добавление рейса");

            stage.setOnHiding(event -> loadFlightsFromServer());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть страницу добавления рейса.");
        }
    }
}
