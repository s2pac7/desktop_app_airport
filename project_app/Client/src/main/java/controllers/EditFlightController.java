package controllers;

import runClient.Connect;
import Pojo.Aircraft;
import Pojo.Flight;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static check.Dialog.showAlert;
import static check.TimeFieldConfigurator.configureTimeField;

public class EditFlightController {

    @FXML
    private Button backButton;

    @FXML
    private Button editFlightButton;

    @FXML
    private TextField editFlightNumberField;

    @FXML
    private TextField editDepartureAirportField;

    @FXML
    private TextField editArrivalAirportField;

    @FXML
    private DatePicker editDepartureDatePicker;

    @FXML
    private TextField editDepartureTimeField;

    @FXML
    private DatePicker editArrivalDatePicker;

    @FXML
    private TextField editArrivalTimeField;

    @FXML
    private ComboBox<String> aircraftCombo;

    @FXML
    private RadioButton toDirectionRadioButton;

    @FXML
    private RadioButton fromDirectionRadioButton;


    private ToggleGroup flightDirectionGroup;

    private Flight selectedFlight;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    void initialize() {
        loadAircraftNames();
        setupFlightDirectionToggleGroup();
        configureTimeField(editDepartureTimeField);
        configureTimeField(editArrivalTimeField);

        backButton.setOnAction(event -> closeWindow());
        editFlightButton.setOnAction(event -> updateFlight());
    }

    public void setFlightData(Flight flight) {
        this.selectedFlight = flight;

        editFlightNumberField.setText(flight.getFlightNumber());
        editDepartureAirportField.setText(flight.getDepartureAirport());
        editArrivalAirportField.setText(flight.getArrivalAirport());
        editDepartureDatePicker.setValue(flight.getDepartureTime().toLocalDate());
        editDepartureTimeField.setText(flight.getDepartureTime().toLocalTime().format(TIME_FORMATTER));
        editArrivalDatePicker.setValue(flight.getArrivalTime().toLocalDate());
        editArrivalTimeField.setText(flight.getArrivalTime().toLocalTime().format(TIME_FORMATTER));

        if ("Aviasales".equals(flight.getDepartureAirport())) {
            toDirectionRadioButton.setSelected(true);
            editDepartureAirportField.setEditable(false);
        } else if ("Aviasales".equals(flight.getArrivalAirport())) {
            fromDirectionRadioButton.setSelected(true);
            editArrivalAirportField.setEditable(false);
        }

        Connect.client.sendMessage("getAircraftByName");
        Connect.client.sendMessage(flight.getNameAircraft());
        try {
            Aircraft selectedAircraft = (Aircraft) Connect.client.readObject();
            if (selectedAircraft != null) {
                aircraftCombo.setValue(selectedAircraft.getAircraftName());
            } else {
                showAlert("Ошибка", "Самолет не найден.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось получить данные о самолете.");
        }
    }
    private void loadAircraftNames() {

        Connect.client.sendMessage("getAircraftNames");
        @SuppressWarnings("unchecked")
        List<String> aircraftNames = (List<String>) Connect.client.readObject();

        if (aircraftNames == null || aircraftNames.isEmpty()) {
            showAlert("Info", "Список самолетов пуст.");
            return;
        }

        aircraftCombo.getItems().clear();
        aircraftCombo.getItems().addAll(aircraftNames);
    }

    private void setupFlightDirectionToggleGroup() {
        flightDirectionGroup = new ToggleGroup();
        toDirectionRadioButton.setToggleGroup(flightDirectionGroup);
        fromDirectionRadioButton.setToggleGroup(flightDirectionGroup);

        flightDirectionGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toDirectionRadioButton.isSelected()) {
                editDepartureAirportField.setText("Aviasales");
                editDepartureAirportField.setEditable(false);
                editArrivalAirportField.setEditable(true);
            } else if (fromDirectionRadioButton.isSelected()) {
                editArrivalAirportField.setText("Aviasales");
                editArrivalAirportField.setEditable(false);
                editDepartureAirportField.setEditable(true);
            }
        });
    }

    private void updateFlight() {
        if (editFlightNumberField.getText().isEmpty() ||
                editDepartureAirportField.getText().isEmpty() ||
                editArrivalAirportField.getText().isEmpty() ||
                editDepartureDatePicker.getValue() == null ||
                editDepartureTimeField.getText().isEmpty() ||
                editArrivalDatePicker.getValue() == null ||
                editArrivalTimeField.getText().isEmpty() ||
                aircraftCombo.getValue() == null) {
            showAlert("Error", "Все поля должны быть заполнены");
            return;
        }

        try {
            LocalDateTime departureTime = parseDateTime(editDepartureDatePicker, editDepartureTimeField);
            LocalDateTime arrivalTime = parseDateTime(editArrivalDatePicker, editArrivalTimeField);

            Connect.client.sendMessage("getAircraftByName");
            Connect.client.sendObject(aircraftCombo.getValue());
            Aircraft selectedAircraft = (Aircraft) Connect.client.readObject();

            if (selectedAircraft == null) {
                showAlert("Error", "Выбранный самолет недействителен");
                return;
            }

            selectedFlight.setFlightNumber(editFlightNumberField.getText());
            selectedFlight.setDepartureAirport(editDepartureAirportField.getText());
            selectedFlight.setArrivalAirport(editArrivalAirportField.getText());
            selectedFlight.setDepartureTime(departureTime);
            selectedFlight.setArrivalTime(arrivalTime);
            selectedFlight.setAircraftID(selectedAircraft.getId());

            Connect.client.sendMessage("updateFlight");
            Connect.client.sendObject(selectedFlight);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Рейс успешно обновлен");
                closeWindow();
            } else {
                showAlert("Error", "Не удалось обновить рейс");
            }
        } catch (DateTimeParseException e) {
            showAlert("Error", "Введите действительные даты и время");
        }
    }

    private LocalDateTime parseDateTime(DatePicker datePicker, TextField timeField) {
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText(), TIME_FORMATTER);
        return LocalDateTime.of(date, time);
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
