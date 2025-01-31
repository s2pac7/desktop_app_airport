package controllers;

import Patterns.builders.UserBuilder;
import javafx.scene.control.*;
import runClient.Connect;
import Pojo.User;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;

public class AddUserController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField addUsernameField;

    @FXML
    private PasswordField addPasswordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Button addUserButton;

    @FXML
    private Button backButton;

    @FXML
    void initialize() {
        roleCombo.getItems().addAll("admin", "user");

        addUserButton.setOnAction(event -> addUser());
        backButton.setOnAction(event -> closeWindow());
    }

    private void addUser() {
        String username = addUsernameField.getText().trim();
        String password = addPasswordField.getText().trim();
        String role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Все поля должны быть заполнены");
            return;
        }

        try {
            addUserButton.setDisable(true);

            User newUser = new UserBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setRole(role)
                    .build();

            Connect.client.sendMessage("addUser");
            Connect.client.sendObject(newUser);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Пользователь успешно добавлен");
                closeWindow();
            } else {
                showAlert("Error", response);
            }
        } catch (Exception e) {
            showAlert("Error", "Не удалось добавить пользователя: " + e.getMessage());
            e.printStackTrace();
        } finally {
            addUserButton.setDisable(false);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
