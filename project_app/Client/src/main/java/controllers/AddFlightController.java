package controllers;

import Patterns.builders.FlightBuilder;
import runClient.Connect;
import Pojo.Aircraft;
import Pojo.Flight;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;
import static check.TimeFieldConfigurator.configureTimeField;

public class AddFlightController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addFlightButton;

    @FXML
    private TextField addFlightNumberField;

    @FXML
    private TextField addDepartureAirportField;

    @FXML
    private DatePicker addDepartureDatePicker;

    @FXML
    private TextField addDepartureTimeField;

    @FXML
    private TextField addArrivalAirportField;

    @FXML
    private DatePicker addArrivalDatePicker;

    @FXML
    private TextField addArrivalTimeField;

    @FXML
    private ComboBox<String> aircraftCombo;

    @FXML
    private Button backButton;

    @FXML
    private RadioButton toDirectionRadioButton;

    @FXML
    private RadioButton fromDirectionRadioButton;

    private ToggleGroup flightDirectionGroup;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    void initialize() {
        setupFlightDirectionToggleGroup();

        loadAircraftNames();

        configureTimeField(addDepartureTimeField);
        configureTimeField(addArrivalTimeField);
        addFlightButton.setOnAction(event -> addFlight());

        backButton.setOnAction(event -> closeWindow());
    }

    private void setupFlightDirectionToggleGroup() {
        flightDirectionGroup = new ToggleGroup();
        toDirectionRadioButton.setToggleGroup(flightDirectionGroup);
        fromDirectionRadioButton.setToggleGroup(flightDirectionGroup);

        flightDirectionGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toDirectionRadioButton.isSelected()) {
                addDepartureAirportField.setText("Aviasales");
                addDepartureAirportField.setEditable(false);
                addArrivalAirportField.setEditable(true);
                addArrivalAirportField.clear();
            } else if (fromDirectionRadioButton.isSelected()) {
                addArrivalAirportField.setText("Aviasales");
                addArrivalAirportField.setEditable(false);
                addDepartureAirportField.setEditable(true);
                addDepartureAirportField.clear();
            }
        });
    }

    private void loadAircraftNames() {
        Connect.client.sendMessage("getAircraftNames");
        @SuppressWarnings("unchecked")
        List<String> aircraftNames = (List<String>) Connect.client.readObject();

        if (aircraftNames == null || aircraftNames.isEmpty()) {
            showAlert("Info", "Список самолетов пуст.");
            return;
        }

        aircraftCombo.getItems().addAll(aircraftNames);
    }

    private void addFlight() {
        String selectedAircraftName = aircraftCombo.getValue();
        if (selectedAircraftName == null) {
            showAlert("Error", "Выберите самолет.");
            return;
        }

        Connect.client.sendMessage("getAircraftByName");
        Connect.client.sendObject(selectedAircraftName);
        Aircraft selectedAircraft = (Aircraft) Connect.client.readObject();

        if (selectedAircraft == null) {
            showAlert("Error", "Не удалось найти самолет с таким именем.");
            return;
        }

        try {
            LocalDateTime departureDateTime = parseDateTime(addDepartureDatePicker, addDepartureTimeField);
            LocalDateTime arrivalDateTime = generateRandomArrivalTime(departureDateTime);

            addArrivalDatePicker.setValue(arrivalDateTime.toLocalDate());
            addArrivalTimeField.setText(arrivalDateTime.toLocalTime().format(TIME_FORMATTER));

            Flight newFlight = new FlightBuilder()
                    .setAircraftID(selectedAircraft.getId())
                    .setFlightNumber(addFlightNumberField.getText())
                    .setDepartureAirport(addDepartureAirportField.getText())
                    .setArrivalAirport(addArrivalAirportField.getText())
                    .setDepartureTime(departureDateTime)
                    .setArrivalTime(arrivalDateTime)
                    .build();

            Connect.client.sendMessage("addFlight");
            Connect.client.sendObject(newFlight);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Рейс добавлен успешно.");
                closeWindow();
            } else {
                showAlert("Error", response);
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private LocalDateTime parseDateTime(DatePicker datePicker, TextField timeField) {
        if (datePicker.getValue() == null || timeField.getText().isEmpty()) {
            throw new IllegalArgumentException("Введите дату и время.");
        }

        try {
            LocalTime time = LocalTime.parse(timeField.getText(), TIME_FORMATTER);
            return LocalDateTime.of(datePicker.getValue(), time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Введите время в формате HH:mm.");
        }
    }

    private LocalDateTime generateRandomArrivalTime(LocalDateTime departureDateTime) {
        Random random = new Random();
        int hoursToAdd = 1 + random.nextInt(5);
        int minutesToAdd = random.nextInt(60);
        return departureDateTime.plusHours(hoursToAdd).plusMinutes(minutesToAdd);
    }


    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
