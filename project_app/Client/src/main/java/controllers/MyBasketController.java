package controllers;

import Pojo.Flight;
import Pojo.Ticket;
import Session.SessionData;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import java.util.List;

import static check.Dialog.showAlert;

public class MyBasketController {

    @FXML
    private TableColumn<Ticket, Integer> flightIDColumn;

    @FXML
    private TableColumn<Ticket, String> seatsColumn;

    @FXML
    private TableColumn<Ticket, String> classSeatColumn;

    @FXML
    private TableColumn<Ticket, Double> priceColumn;

    @FXML
    private TableColumn<Ticket, String> statusColumn;

    @FXML
    private TableColumn<Ticket, Integer> idColumn;

    @FXML
    private TableColumn<Flight, String> flightNumberColumn;

    @FXML
    private TableColumn<Flight, String> departureAirportColumn;

    @FXML
    private TableColumn<Flight, String> arrivalAirportColumn;

    @FXML
    private TableColumn<Flight, String> departureTimeColumn;

    @FXML
    private TableColumn<Flight, String> arrivalTimeColumn;

    @FXML
    private TableView<Ticket> infoTableTicket;

    @FXML
    private TableView<Flight> infoTableFlight;

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button updateButton;

    @FXML
    private void initialize() {
        setupTicketTableColumns();
        setupFlightTableColumns();

        backToMenuButton.setOnAction(event -> backToMenu());
        updateButton.setOnAction(event -> updateTableData());

        // Добавляем слушатели для синхронизации выбора строк между таблицами
        infoTableTicket.getSelectionModel().selectedItemProperty().addListener(this::onTicketSelected);
        infoTableFlight.getSelectionModel().selectedItemProperty().addListener(this::onFlightSelected);

        // Настраиваем стиль строк
        infoTableTicket.setRowFactory(tableView -> {
            TableRow<Ticket> row = new TableRow<>();
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle("-fx-background-color: #add8e6;"); // Нежно-синий
                } else {
                    row.setStyle(""); // Убираем стиль
                }
            });
            return row;
        });

        infoTableFlight.setRowFactory(tableView -> {
            TableRow<Flight> row = new TableRow<>();
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle("-fx-background-color: #add8e6;"); // Нежно-синий
                } else {
                    row.setStyle(""); // Убираем стиль
                }
            });
            return row;
        });

        // Подключение CSS
        infoTableTicket.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        infoTableFlight.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private void setupTicketTableColumns() {
        flightIDColumn.setCellValueFactory(new PropertyValueFactory<>("flightID"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        classSeatColumn.setCellValueFactory(new PropertyValueFactory<>("ticketClass"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFlightTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        departureAirportColumn.setCellValueFactory(new PropertyValueFactory<>("departureAirport"));
        arrivalAirportColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalAirport"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
    }

    private void onTicketSelected(ObservableValue<? extends Ticket> observable, Ticket oldValue, Ticket newValue) {
        if (newValue != null) {
            int flightID = newValue.getFlightID();
            selectFlightByID(flightID);
        }
    }

    private void onFlightSelected(ObservableValue<? extends Flight> observable, Flight oldValue, Flight newValue) {
        if (newValue != null) {
            int flightID = newValue.getId();
            selectTicketByFlightID(flightID);
        }
    }

    private void selectFlightByID(int flightID) {
        for (Flight flight : infoTableFlight.getItems()) {
            if (flight.getId() == flightID) {
                infoTableFlight.getSelectionModel().select(flight);
                infoTableFlight.scrollTo(flight); // Прокручиваем к найденной записи, если она не видна
                break;
            }
        }
    }

    private void selectTicketByFlightID(int flightID) {
        for (Ticket ticket : infoTableTicket.getItems()) {
            if (ticket.getFlightID() == flightID) {
                infoTableTicket.getSelectionModel().select(ticket);
                infoTableTicket.scrollTo(ticket); // Прокручиваем к найденной записи, если она не видна
                break;
            }
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

    private void updateTableData() {
        try {
            int currentUserId = SessionData.getCurrentUserId();

            if (currentUserId == -1) {
                showAlert("Ошибка", "Идентификатор пользователя не установлен. Пожалуйста, повторите вход.");
                return;
            }
            Connect.client.sendMessage("getUserTickets");
            Connect.client.sendMessage(currentUserId);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                List<Ticket> tickets = (List<Ticket>) Connect.client.readObject();
                System.out.println("Полученные билеты от сервера:");
                tickets.forEach(System.out::println);

                Connect.client.sendMessage("OK");

                List<Flight> flights = (List<Flight>) Connect.client.readObject();
                System.out.println("Полученные рейсы от сервера:");
                flights.forEach(System.out::println);

                ObservableList<Ticket> ticketData = FXCollections.observableArrayList(tickets);
                Platform.runLater(() -> infoTableTicket.setItems(ticketData));

                ObservableList<Flight> flightData = FXCollections.observableArrayList(flights);
                Platform.runLater(() -> infoTableFlight.setItems(flightData));
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные таблиц: " + e.getMessage());
        }
    }
}
