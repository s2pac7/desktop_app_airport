package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import runClient.Connect;
import Pojo.Passenger;

import java.time.LocalDate;
import java.time.Period;

import static check.Dialog.showAlert;

public class EditPassengerController {

    @FXML
    private Button backButton;

    @FXML
    private DatePicker editDateOfBirthField;

    @FXML
    private TextField editNameField;

    @FXML
    private Button editPassengerButton;

    @FXML
    private TextField editPassportNumberField;

    @FXML
    private ComboBox<String> countryCodeBox;

    @FXML
    private TextField editPhoneNumberField;

    @FXML
    private TextField editSurnameField;

    private Passenger selectedPassenger;

    @FXML
    void initialize() {
        countryCodeBox.setItems(FXCollections.observableArrayList("+375", "+7"));
        setupCountryCodeIcons();

        editPassengerButton.setOnAction(event -> updatePassenger());
        backButton.setOnAction(event -> closeWindow());

        editPhoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            editPhoneNumberField.setText(formatPhoneNumber(newValue));
        });
    }

    private void setupCountryCodeIcons() {
        countryCodeBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView flagView = new ImageView();
                    flagView.setFitWidth(20);
                    flagView.setFitHeight(15);
                    if ("+375".equals(item)) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/belarus.png")));
                    } else if ("+7".equals(item)) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/russia.png")));
                    }
                    setText(item);
                    setGraphic(flagView);
                }
            }
        });

        countryCodeBox.setButtonCell(countryCodeBox.getCellFactory().call(null));
    }

    public void setPassengerData(Passenger passenger) {
        this.selectedPassenger = passenger;

        editNameField.setText(passenger.getName());
        editSurnameField.setText(passenger.getSurname());
        editDateOfBirthField.setValue(LocalDate.parse(passenger.getDateOfBirth()));

        String[] phoneParts = passenger.getPhoneNumber().split(" ", 2);
        if (phoneParts.length == 2) {
            countryCodeBox.setValue(phoneParts[0]);
            editPhoneNumberField.setText(phoneParts[1]);
        }

        editPassportNumberField.setText(passenger.getPassportNumber());
    }

    private void updatePassenger() {
        if (!validateFields()) return;

        selectedPassenger.setName(editNameField.getText().trim());
        selectedPassenger.setSurname(editSurnameField.getText().trim());
        selectedPassenger.setDateOfBirth(editDateOfBirthField.getValue().toString());
        selectedPassenger.setPassportNumber(editPassportNumberField.getText().trim());
        selectedPassenger.setPhoneNumber(countryCodeBox.getValue() + " " + editPhoneNumberField.getText().trim());

        Connect.client.sendMessage("updatePassenger");
        Connect.client.sendObject(selectedPassenger);

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            showAlert("Success", "Пассажир обновлен успешно.");
            closeWindow();
        } else {
            showAlert("Error", "Не удалось обновить информацию о пассажире.");
        }
    }

    private boolean validateFields() {
        if (editNameField.getText().isEmpty() || editSurnameField.getText().isEmpty() ||
                editDateOfBirthField.getValue() == null || editPassportNumberField.getText().isEmpty() ||
                editPhoneNumberField.getText().isEmpty()) {
            showAlert("Error", "Все поля должны быть заполнены.");
            return false;
        }

        if (!isPassportNumberValid(editPassportNumberField.getText())) {
            showAlert("Error", "Номер паспорта должен состоять из двух букв и семи цифр.");
            return false;
        }

        if (!isAgeValid(editDateOfBirthField.getValue())) {
            showAlert("Error", "Пассажир должен быть старше 18 лет.");
            return false;
        }

        if (!isPhoneNumberValid(editPhoneNumberField.getText())) {
            showAlert("Error", "Номер телефона должен содержать минимум 9 цифр.");
            return false;
        }

        return true;
    }

    private String formatPhoneNumber(String input) {
        String digits = input.replaceAll("\\D", "");
        StringBuilder formatted = new StringBuilder();
        if (digits.length() > 0) {
            formatted.append("(");
            formatted.append(digits.substring(0, Math.min(digits.length(), 2)));
            formatted.append(")");
        }
        if (digits.length() > 2) {
            formatted.append(" ");
            formatted.append(digits.substring(2, Math.min(digits.length(), 5)));
        }
        if (digits.length() > 5) {
            formatted.append("-");
            formatted.append(digits.substring(5, Math.min(digits.length(), 9)));
        }
        return formatted.toString();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        String digits = phoneNumber.replaceAll("\\D", "");
        return digits.length() >= 9;
    }

    private boolean isPassportNumberValid(String passportNumber) {
        return passportNumber.matches("^[A-Z]{2}[0-9]{7}$");
    }

    private boolean isAgeValid(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
