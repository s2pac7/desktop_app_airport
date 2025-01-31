package controllers;

import Session.SessionData;
import check.Dialog;
import javafx.scene.control.PasswordField;
import runClient.Connect;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangePasswordController {

    @FXML
    private Button backButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private PasswordField confirmNewPasswordField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    void initialize() {
        backButton.setOnAction(event -> goBack());

        changePasswordButton.setOnAction(event -> changePassword());
    }

    private void goBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showAlert("Error", "Ошибка при возврате назад");
        }
    }

    private void changePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Dialog.showAlert("Error", "Заполните все поля.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Dialog.showAlert("Error", "Новый пароль и его подтверждение не совпадают.");
            return;
        }

        Connect.client.sendMessage("changePassword");
        Connect.client.sendObject(SessionData.getCurrentUserId());
        Connect.client.sendObject(currentPassword);
        Connect.client.sendObject(newPassword);

        String response = (String) Connect.client.readObject();

        if ("OK".equals(response)) {
            Dialog.showAlert("Success", "Пароль успешно изменён.");

            Stage stage = (Stage) changePasswordButton.getScene().getWindow();
            stage.close();
        } else if ("Invalid current password".equals(response)) {
            Dialog.showAlert("Error", "Текущий пароль указан неверно.");
        } else {
            Dialog.showAlert("Error", "Ошибка смены пароля: " + response);
        }
    }
}
