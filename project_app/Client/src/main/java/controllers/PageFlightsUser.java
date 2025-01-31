package controllers;

import Pojo.Aircraft;
import Session.SessionData;
import runClient.Connect;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static check.Dialog.showAlert;

public class PageFlightsUser {

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button addBalance;

    @FXML
    private Button buyTicket;

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
    private TableColumn<Aircraft, String> nameAircraftColumn;

    @FXML
    private TableView<Flight> infoTable;

    @FXML
    private Button showButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private TextField balanceField;

    @FXML
    void initialize() {
        setupTableColumns();
        loadUserBalance();

        backToMenuButton.setOnAction(event -> backToMenu());
        showButton.setOnAction(event -> loadFlightsFromServer());
        searchButton.setOnAction(event -> searchFlightByNumber());
        addBalance.setOnAction(event -> openAddBalancePage());
        buyTicket.setOnAction(event -> openBuyTicketPage());
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

    private void loadUserBalance() {
        int currentUserId = SessionData.getCurrentUserId();

        if (currentUserId == -1) {
            showAlert("Ошибка", "Идентификатор пользователя не установлен. Пожалуйста, повторите вход.");
            return;
        }

        try {
            Connect.client.sendMessage("getUserBalance");
            Connect.client.sendMessage(String.valueOf(currentUserId));

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                String balance = (String) Connect.client.readObject();
                Platform.runLater(() -> balanceField.setText(balance));
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить баланс: " + e.getMessage());
        }
    }

    private void openBuyTicketPage() {
        Flight selectedFlight = infoTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/buyProcess.fxml"));
                Parent root = loader.load();

                BuyTicketController controller = loader.getController();
                controller.setFlightData(selectedFlight);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Для покупки билета не выбран рейс");
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

    private void openAddBalancePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addBalance.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Balance");

            // Добавляем обработчик для обновления баланса при закрытии окна
            stage.setOnHiding(event -> loadUserBalance());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть страницу добавления баланса.");
        }
    }

    private void loadFlightsFromServer() {
        try {
            Connect.client.sendMessage("getAllFlightsForBuy");

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
}
