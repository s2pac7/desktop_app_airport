package controllers;

import Pojo.Flight;
import Session.SessionData;
import runClient.Connect;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static check.Dialog.showAlert;
import static check.FieldValidator.configureTimeField;

public class BuyTicketController {

    @FXML
    private TextField FlightNumberField;

    @FXML
    private TextField DepartureAirportField;

    @FXML
    private TextField ArrivalAirportField;

    @FXML
    private DatePicker DepartureDatePicker;

    @FXML
    private TextField DepartureTimeField;

    @FXML
    private DatePicker ArrivalDatePicker;

    @FXML
    private TextField ArrivalTimeField;

    @FXML
    private TextField PriceField;

    @FXML
    private Button backButton;

    @FXML
    private Button buyButton;

    @FXML
    private Button s10economy, s11economy, s12economy, s13economy, s14economy,
            s15economy, s16economy, s17economy, s18economy,
            s1business, s2business, s3business, s4business, s5business, s6business,
            s7economy, s8economy, s9economy;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private Flight selectedFlight;
    private Button activeButton = null; // Текущая выбранная кнопка

    @FXML
    void initialize() {
        configureTimeField(DepartureTimeField);
        configureTimeField(ArrivalTimeField);

        generateRandomPrice();

        backButton.setOnAction(event -> closeWindow());
        buyButton.setOnAction(event -> processTicketPurchase());
        setSeatButtonActions();
    }

    public void setFlightData(Flight flight) {
        this.selectedFlight = flight;

        FlightNumberField.setText(flight.getFlightNumber());
        DepartureAirportField.setText(flight.getDepartureAirport());
        ArrivalAirportField.setText(flight.getArrivalAirport());
        DepartureDatePicker.setValue(flight.getDepartureTime().toLocalDate());
        DepartureTimeField.setText(flight.getDepartureTime().toLocalTime().format(TIME_FORMATTER));
        ArrivalDatePicker.setValue(flight.getArrivalTime().toLocalDate());
        ArrivalTimeField.setText(flight.getArrivalTime().toLocalTime().format(TIME_FORMATTER));

        loadOccupiedSeats();
    }

    private void processTicketPurchase() {
        if (selectedFlight == null) {
            showAlert("Error", "Рейс не выбран");
            return;
        }

        int userId = SessionData.getCurrentUserId();
        if (userId == -1) {
            showAlert("Error", "Пользователь не вошел в систему. Пожалуйста, войдите, чтобы продолжить");
            return;
        }

        if (activeButton == null) {
            showAlert("Error", "Место не выбрано. Пожалуйста, выберите место");
            return;
        }

        String selectedSeat = activeButton.getText();
        String flightId = String.valueOf(selectedFlight.getId());
        String price = PriceField.getText();

        System.out.println("Sending data to server:");
        System.out.println("User ID: " + userId);
        System.out.println("Flight ID: " + flightId);
        System.out.println("Selected Seat: " + selectedSeat);
        System.out.println("Price: " + price);

        try {
            Connect.client.sendMessage("buyTicket");
            Connect.client.sendObject(userId);
            Connect.client.sendObject(flightId);
            Connect.client.sendObject(selectedSeat);
            Connect.client.sendObject(price);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                // Получаем файл чека в виде массива байтов
                byte[] fileBytes = (byte[]) Connect.client.readObject();

                // Создаем путь для сохранения чека
                String receiptsDir = System.getProperty("user.dir") + File.separator + "Receipts";
                Path receiptPath = Paths.get(receiptsDir, "ticket_receipt_" + System.currentTimeMillis() + ".txt");
                Files.createDirectories(receiptPath.getParent()); // Убедиться, что директория существует
                Files.write(receiptPath, fileBytes);

                showAlert("Success", "Билет успешно куплен. Квитанция сохранена: " + receiptPath.toAbsolutePath());
                closeWindow();
            } else {
                showAlert("Error", "Не удалось купить билет: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Произошла ошибка: " + e.getMessage());
        }
    }

    private void generateRandomPrice() {
        Random random = new Random();
        int price = 500 + random.nextInt(401);
        PriceField.setText(String.valueOf(price));
    }

    private void setSeatButtonActions() {
        Button[] seatButtons = {
                s1business, s2business, s3business, s4business, s5business, s6business,
                s7economy, s8economy, s9economy, s10economy, s11economy, s12economy,
                s13economy, s14economy, s15economy, s16economy, s17economy, s18economy
        };

        for (Button seatButton : seatButtons) {
            seatButton.setOnAction(event -> {
                selectSeat(seatButton);
            });
        }
    }

    private void loadOccupiedSeats() {
        try {
            Connect.client.sendMessage("checkSeats");
            Connect.client.sendObject(selectedFlight.getId());

            // Получаем список занятых мест
            List<String> occupiedSeats = (List<String>) Connect.client.readObject();
            blockOccupiedSeats(occupiedSeats);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Не удалось загрузить занятые места: " + e.getMessage());
        }
    }

    private void blockOccupiedSeats(List<String> occupiedSeats) {
        Button[] seatButtons = {
                s1business, s2business, s3business, s4business, s5business, s6business,
                s7economy, s8economy, s9economy, s10economy, s11economy, s12economy,
                s13economy, s14economy, s15economy, s16economy, s17economy, s18economy
        };

        for (Button seatButton : seatButtons) {
            if (occupiedSeats.contains(seatButton.getText())) {
                seatButton.setDisable(true); // Блокируем кнопку, чтобы нельзя было выбрать место
                seatButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Подсвечиваем красным
            }
        }
    }

    private void selectSeat(Button seatButton) {
        if (activeButton != null) {
            activeButton.setStyle(""); // Сбросить стиль предыдущей кнопки
        }

        activeButton = seatButton;
        seatButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
