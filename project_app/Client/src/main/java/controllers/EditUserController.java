package controllers;

import runClient.Connect;
import Pojo.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;

public class EditUserController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField editUsernameField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Button editUserButton;

    @FXML
    private Button backButton;

    private User selectedUser;

    @FXML
    void initialize() {
        roleCombo.getItems().addAll("admin", "user");

        editUserButton.setOnAction(event -> updateUser());
        backButton.setOnAction(event -> closeWindow());
    }

    public void setUserData(User user) {
        this.selectedUser = user;
        editUsernameField.setText(user.getUsername());
        roleCombo.setValue(user.getRole());
    }

    private void updateUser() {
        if (editUsernameField.getText().isEmpty() || roleCombo.getValue() == null) {
            showAlert("Error", "Все поля должны быть заполнены");
            return;
        }
        selectedUser.setUsername(editUsernameField.getText().trim());
        selectedUser.setRole(roleCombo.getValue());

        Connect.client.sendMessage("updateUser");
        Connect.client.sendObject(selectedUser);

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            showAlert("Success", "Пользователь обновлен успешно");
            closeWindow();
        } else {
            showAlert("Error", "Не удалось обновить пользователя");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

}
