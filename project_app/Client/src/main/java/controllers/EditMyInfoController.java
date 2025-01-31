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

public class EditMyInfoController {

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

    @FXML
    private TextField editUsernameField;

    private Passenger passenger;

    public void setPassengerData(Passenger passenger) {
        this.passenger = passenger;

        editNameField.setText(passenger.getName());
        editSurnameField.setText(passenger.getSurname());
        editDateOfBirthField.setValue(LocalDate.parse(passenger.getDateOfBirth()));
        editPassportNumberField.setText(passenger.getPassportNumber());
        String[] phoneParts = passenger.getPhoneNumber().split(" ", 2);
        if (phoneParts.length == 2) {
            countryCodeBox.setValue(phoneParts[0]);
            editPhoneNumberField.setText(phoneParts[1]);
        }
        editUsernameField.setText(passenger.getUsername());
    }

    @FXML
    void initialize() {
        countryCodeBox.setItems(FXCollections.observableArrayList("+375", "+7"));
        countryCodeBox.getSelectionModel().selectFirst();

        editPhoneNumberField.textProperty().addListener((obs, oldValue, newValue) -> {
            editPhoneNumberField.setText(formatPhoneNumber(newValue));
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

        backButton.setOnAction(event -> closeWindow());

        editPassengerButton.setOnAction(event -> savePassengerChanges());
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void savePassengerChanges() {
        if (passenger != null && validateFields()) {
            passenger.setName(editNameField.getText().trim());
            passenger.setSurname(editSurnameField.getText().trim());
            passenger.setDateOfBirth(editDateOfBirthField.getValue().toString());
            passenger.setPassportNumber(editPassportNumberField.getText().trim());
            passenger.setPhoneNumber(countryCodeBox.getValue() + " " + editPhoneNumberField.getText().trim());

            String newUsername = editUsernameField.getText().trim();

            Connect.client.sendMessage("updatePassengerAndUser");
            Connect.client.sendObject(passenger);
            Connect.client.sendObject(newUsername);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Passenger updated successfully.");
                closeWindow();
            } else {
                showAlert("Error", "Failed to update passenger: " + response);
            }
        }
    }

    private boolean validateFields() {
        try {
            if (editNameField.getText().isEmpty() ||
                    editSurnameField.getText().isEmpty() ||
                    editPassportNumberField.getText().isEmpty() ||
                    editPhoneNumberField.getText().isEmpty() ||
                    editDateOfBirthField.getValue() == null) {
                showAlert("Error", "All fields must be filled.");
                return false;
            }

            if (!isPhoneNumberValid(editPhoneNumberField.getText())) {
                showAlert("Error", "Phone number must contain at least 9 digits.");
                return false;
            }

            if (!isPassportNumberValid(editPassportNumberField.getText())) {
                showAlert("Error", "Passport number must consist of two letters and seven digits (e.g., AB1234567)");
                return false;
            }

            if (!isAgeValid(editDateOfBirthField.getValue())) {
                showAlert("Error", "User must be at least 18 years old.");
                return false;
            }

            return true;
        } catch (Exception e) {
            showAlert("Error", "An error occurred while validating fields: " + e.getMessage());
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
