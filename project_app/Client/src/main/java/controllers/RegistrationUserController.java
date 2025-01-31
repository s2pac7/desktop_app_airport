package controllers;

import Patterns.builders.PassengerBuilder;
import Session.SessionData;
import check.Dialog;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import runClient.Connect;
import Pojo.Passenger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class RegistrationUserController {

    @FXML
    private Button backRegButton;

    @FXML
    private Button completeReg;

    @FXML
    private DatePicker dateOfBirthField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField passportNumberField;

    @FXML
    private ComboBox<String> countryCodeBox;

    @FXML
    private TextField maskedPhoneField;

    @FXML
    private void initialize() {
        countryCodeBox.setItems(FXCollections.observableArrayList("+375", "+7"));
        countryCodeBox.getSelectionModel().selectFirst();

        maskedPhoneField.textProperty().addListener((obs, oldValue, newValue) -> {
            maskedPhoneField.setText(formatPhoneNumber(newValue));
        });

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

        countryCodeBox.setButtonCell(new ListCell<>() {
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

        backRegButton.setOnAction(event -> {
            try {
                goToAuthorization();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        completeReg.setOnAction(event -> {
            try {
                registerPassenger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void goToAuthorization() throws IOException {
        backRegButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autorization.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void registerPassenger() throws IOException {
        if (checkFields()) {
            Passenger passenger = new PassengerBuilder()
                    .setUserId(SessionData.getCurrentUserId())
                    .setName(nameField.getText())
                    .setSurname(surnameField.getText())
                    .setPassportNumber(passportNumberField.getText())
                    .setDateOfBirth(dateOfBirthField.getValue().toString())
                    .setPhoneNumber(countryCodeBox.getValue() + " " + maskedPhoneField.getText())
                    .setBalance(BigDecimal.ZERO)
                    .build();

            Connect.client.sendMessage("registrationPassenger");
            Connect.client.sendObject(passenger);

            String response = Connect.client.readMessage();
            if ("OK".equals(response)) {
                Dialog.showAlert("Success", "Passenger registration completed successfully.");
                goToAuthorization();
            } else {
                Dialog.showAlert("Error", "Failed to register passenger: " + response);
            }
        }
    }

    private boolean checkFields() {
        try {
            if (nameField.getText().isEmpty() ||
                    surnameField.getText().isEmpty() ||
                    passportNumberField.getText().isEmpty() ||
                    maskedPhoneField.getText().isEmpty() ||
                    dateOfBirthField.getValue() == null) {
                Dialog.showAlert("Error", "All fields must be filled.");
                return false;
            }

            if (!isPhoneNumberValid(maskedPhoneField.getText())) {
                Dialog.showAlert("Error", "Phone number must contain at least 9 digits.");
                return false;
            }

            if (!isPassportNumberValid(passportNumberField.getText())) {
                Dialog.showAlert("Error", "Passport number must consist of two letters and seven digits (e.g., AB1234567).");
                return false;
            }

            if (!isAgeValid(dateOfBirthField.getValue())) {
                Dialog.showAlert("Error", "User must be at least 18 years old.");
                return false;
            }

            return true;
        } catch (Exception e) {
            Dialog.showAlert("Error", "An error occurred while validating fields: " + e.getMessage());
            return false;
        }
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
}
