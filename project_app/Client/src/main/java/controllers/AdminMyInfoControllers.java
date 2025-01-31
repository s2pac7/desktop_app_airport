package controllers;

import Session.SessionData;
import check.Dialog;
import javafx.scene.control.*;
import runClient.Connect;
import Pojo.Passenger;
import Pojo.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static check.Dialog.showAlert;
import static check.Dialog.showConfirmationDialog;

public class AdminMyInfoControllers {

    @FXML
    private Button backToMenuButton;

    @FXML
    private TableColumn<Passenger, String> dateOfBirthColumn;

    @FXML
    private TableView<Passenger> infoTable;

    @FXML
    private TableColumn<Passenger, String> nameColumn;

    @FXML
    private TableColumn<Passenger, String> passportNumberColumn;

    @FXML
    private TableColumn<Passenger, String> phoneNumberColumn;

    @FXML
    private TableColumn<Passenger, String> surnameColumn;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Button editAccountButton;
    @FXML
    private TableColumn<User, String> usernameColumn;

    private ObservableList<Passenger> passengerData = FXCollections.observableArrayList();
    private ObservableList<User> userData = FXCollections.observableArrayList();

    private int currentUserId;

    @FXML
    void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        passportNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        backToMenuButton.setOnAction(event -> backToMainMenu());
        deleteAccountButton.setOnAction(event -> deleteAccount());
        editAccountButton.setOnAction(event -> openEditMyInfoScreen());
        changePasswordButton.setOnAction(event -> openChangePasswordScreen());

        currentUserId = SessionData.getCurrentUserId();

        if (currentUserId == -1) {
            System.out.println("Ошибка: currentUserId не установлен в Session.SessionData");
            return;
        }
        loadDataForCurrentUser();
    }

    private void openEditMyInfoScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editMyInfo.fxml"));
            Parent root = loader.load();

            EditMyInfoController editController = loader.getController();

            if (!passengerData.isEmpty()) {
                Passenger passenger = passengerData.get(0);
                editController.setPassengerData(passenger);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Account Information");

            // Обновление таблицы после закрытия окна редактирования
            stage.setOnHiding(event -> loadDataForCurrentUser());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при переходе на экран редактирования аккаунта");
        }
    }

    private void deleteAccount() {
        int userId = SessionData.getCurrentUserId();

        boolean confirmed = showConfirmationDialog("Удаление аккаунта", "Вы уверены, что хотите удалить аккаунт?");
        if (confirmed) {

            Connect.client.sendMessage("deleteUser");
            Connect.client.sendObject(userId);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Аккаунт успешно удален.");
                goToAuthorizationScreen();
            } else {
                showAlert("Error", "Не удалось удалить аккаунт: " + response);
            }
        }
    }

    private void goToAuthorizationScreen() {
        try {
            Stage currentStage = (Stage) deleteAccountButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/autorization.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Authorization");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при переходе на экран авторизации");
        }
    }

    private void openChangePasswordScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/changePassword.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Change Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при открытии окна смены пароля");
        }
    }

    private void loadDataForCurrentUser() {
        if (currentUserId == 0) {
            System.out.println("Ошибка: currentUserId не установлен");
            return;
        }

        List<Passenger> currentUserData = getCurrentUserDataFromServer();

        passengerData.setAll(currentUserData);
        infoTable.setItems(passengerData);
    }

    private List<Passenger> getCurrentUserDataFromServer() {
        System.out.println("Отправка ID пользователя на сервер: " + currentUserId);
        Connect.client.sendMessage("getUserData");
        Connect.client.sendObject(currentUserId);

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {

            Passenger passenger = (Passenger) Connect.client.readObject();
            System.out.println("Пассажир получен: " + passenger);

            Connect.client.sendMessage("OK");

            String username = (String) Connect.client.readObject();
            System.out.println("Username получен: " + username);

            passenger.setUsername(username);
            return List.of(passenger);
        } else {
            System.out.println("Данные пользователя не найдены");
            Dialog.showAlertWithInvalidCredentials();
            return Collections.emptyList();
        }
    }

    private void backToMainMenu() {
        try {
            backToMenuButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminMenu.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке главного меню");
        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("currentUserId установлен: " + currentUserId);
        loadDataForCurrentUser();
    }
}
