package controllers;

import Patterns.builders.PassengerBuilder;
import Patterns.builders.UserBuilder;
import check.Dialog;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import runClient.Connect;
import Pojo.Passenger;
import Pojo.User;
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

import static check.Dialog.*;

public class RegistrationController {

    @FXML
    private Button backRegButton;

    @FXML
    private Button completeReg;

    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private TextField loginRegField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passportNumberField;

    @FXML
    private PasswordField passwordRegField;

    @FXML
    private ComboBox<String> countryCodeBox;

    @FXML
    private TextField maskedPhoneField;

    @FXML
    private void initialize() {
        countryCodeBox.getSelectionModel().selectFirst(); // Default to +375

        maskedPhoneField.textProperty().addListener((obs, oldValue, newValue) -> {
            maskedPhoneField.setText(formatPhoneNumber(newValue));
        });

        backRegButton.setOnAction(event -> {
            try {
                goToAuthorization(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        completeReg.setOnAction(event -> {
            try {
                completeRegistration(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        countryCodeBox.setItems(FXCollections.observableArrayList("+375", "+7"));

        // Настраиваем фабрику ячеек для отображения флагов
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
                    if (item.equals("+375")) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/belarus.png")));
                    } else if (item.equals("+7")) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/russia.png")));
                    }
                    setText(item);
                    setGraphic(flagView);
                }
            }
        });

        // Для отображения выбранного значения с флагом
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
                    if (item.equals("+375")) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/belarus.png")));
                    } else if (item.equals("+7")) {
                        flagView.setImage(new Image(getClass().getResourceAsStream("/flags/russia.png")));
                    }
                    setText(item);
                    setGraphic(flagView);
                }
            }
        });

        // Устанавливаем значение по умолчанию
        countryCodeBox.getSelectionModel().selectFirst();
    }

    void goToAuthorization(ActionEvent event) throws IOException {
        backRegButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autorization.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    void completeRegistration(ActionEvent event) throws IOException {
        if (check()) {
            Dialog.showAlertWithNullInput();
        } else {
            User user = new UserBuilder()
                    .setUsername(loginRegField.getText())
                    .setPassword(passwordRegField.getText())
                    .setRole("user")
                    .build();

            Connect.client.sendMessage("registrationUser");
            Connect.client.sendObject(user);
            System.out.println("User registration sent");

            String message;
            User registeredUser;
            try {
                message = Connect.client.readMessage();
                if ("OK".equals(message)) {
                    registeredUser = (User) Connect.client.readObject();
                } else {
                    if ("This user already exists".equals(message)) {
                        Dialog.showAlertWithExistLogin();
                    } else {
                        System.out.println("User registration error");
                    }
                    return;
                }
            } catch (IOException e) {
                System.out.println("Error reading server response");
                return;
            }

            Passenger passenger = new PassengerBuilder()
                    .setUserId(registeredUser.getId())
                    .setName(nameField.getText())
                    .setSurname(surnameField.getText())
                    .setDateOfBirth(dateOfBirthPicker.getValue().toString())
                    .setPassportNumber(passportNumberField.getText())
                    .setPhoneNumber(countryCodeBox.getValue() + " " + maskedPhoneField.getText())
                    .setBalance(BigDecimal.ZERO)
                    .build();

            Connect.client.sendMessage("registrationPassenger");
            Connect.client.sendObject(passenger);
            System.out.println("Passenger registration sent");

            try {
                message = Connect.client.readMessage();
                if ("OK".equals(message)) {
                    backRegButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/autorization.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    System.out.println("Passenger registration error");
                }
            } catch (IOException e) {
                System.out.println("Error reading server response");
            }
        }
    }

    private boolean check() {
        try {
            if (!isPhoneNumberValid(maskedPhoneField.getText())) {
                Dialog.showAlert("Error", "Phone number must contain at least 9 digits.");
                return true;
            }
            if (!isPassportNumberValid(passportNumberField.getText())) {
                Dialog.showAlert("Error", "Passport number must consist of two letters and seven digits (e.g., AB1234567).");
                return true;
            }
            if (!isAgeValid(dateOfBirthPicker.getValue())) {
                Dialog.showAlert("Error", "User must be at least 18 years old.");
                return true;
            }
            if (nameField.getText().isEmpty() ||
                    surnameField.getText().isEmpty() ||
                    loginRegField.getText().isEmpty() ||
                    passwordRegField.getText().isEmpty() ||
                    dateOfBirthPicker.getValue() == null) {
                Dialog.showAlert("Error", "All fields must be filled.");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error validating fields");
            return true;
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
