package controllers;

import Session.SessionData;
import check.Check;
import check.Dialog;
import javafx.scene.Node;
import runClient.Connect;
import Pojo.Authorization;
import Pojo.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AutorizationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button enterButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registrationButton;

    @FXML
    void initialize() {
        enterButton.setOnAction(event -> {
            authorization(event);
        });

        registrationButton.setOnAction(event -> registration(event));
    }

    public void authorization(ActionEvent event) {
        if (check()) {
            Dialog.showAlertWithInvalidCredentials();
            return;
        }
        try {
            Connect.client.sendMessage("authorization");

            Authorization authorization = new Authorization();
            authorization.setUsername(loginField.getText());
            authorization.setPassword(passwordField.getText());
            Connect.client.sendObject(authorization);

            String message = Connect.client.readMessage();

            if ("OK".equals(message)) {
                User authorizedUser = (User) Connect.client.readObject();
                int userId = authorizedUser.getId();

                SessionData.setCurrentUserIdSession(userId);

                String passengerCheck = Connect.client.readMessage();
                if ("Passenger not found".equals(passengerCheck)) {
                    Dialog.showAlert("Error", "Данные о пассажирах не найдены. Пожалуйста, завершите регистрацию");
                    openRegistrationUserInterface(event);
                    return;
                }

                if ("admin".equals(authorizedUser.getRole())) {
                    System.out.println("Вход выполнен как админ");
                    openAdminInterface(event);
                } else {
                    System.out.println("Вход выполнен как пользователь");
                    openUserInterface(event);
                }
            } else {
                System.out.println("Неверные учетные данные или пользователь не найден");
                Dialog.showAlertWithInvalidCredentials();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении сообщения: " + e.getMessage());
        }
    }

    private void openRegistrationUserInterface(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registrationUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAdminInterface(ActionEvent event) throws IOException {
        enterButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminMenu.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openUserInterface(ActionEvent event) throws IOException {
        enterButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/userMenu.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void registration(ActionEvent event) {
        try {
            registrationButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/registration.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке окна регистрации");
        }
    }

    public boolean check() {
        return loginField.getText().isEmpty() || passwordField.getText().isEmpty();
    }
}
