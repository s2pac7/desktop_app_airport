package controllers;

import runClient.Connect;
import Pojo.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;

public class PageUsersAdmin {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private TableColumn<User, Integer> idUserColumn;

    @FXML
    private TableView<User> infoTable;

    @FXML
    private TableColumn<User, String> roleUserColumn;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button showButton;

    @FXML
    private TableColumn<User, String> usernameUserColumn;


    @FXML
    void initialize() {
        setupTableColumns();

        backToMenuButton.setOnAction(event -> backToMenu());
        addButton.setOnAction(event -> openAddUserPage());
        showButton.setOnAction(event -> loadUsersFromServer());
        deleteButton.setOnAction(event -> deleteSelectedUser());
        editButton.setOnAction(event -> openEditUserPage());
        searchButton.setOnAction(event -> searchUserByUsername());
    }

    private void setupTableColumns() {
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameUserColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleUserColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        searchButton.setOnAction(event -> searchUserByUsername());
    }

    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddUserPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addUser.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setOnHiding(event -> loadUsersFromServer());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditUserPage() {
        User selectedUser = infoTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/editUser.fxml"));
                Parent root = loader.load();

                EditUserController editController = loader.getController();
                editController.setUserData(selectedUser);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.setOnHiding(event -> loadUsersFromServer());

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Ни один пользователь не выбран для редактирования");
        }
    }

    private void loadUsersFromServer() {
        try {
            infoTable.getItems().clear();

            Connect.client.sendMessage("getAllUsers");
            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                List<User> users = (List<User>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<User> userData = FXCollections.observableArrayList(users);
                    infoTable.setItems(userData);
                });
            } else {
                showAlert("Error", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Не удалось загрузить пользователей: " + e.getMessage());
        }
    }

    private void searchUserByUsername() {
        String searchQuery = searchField.getText().trim();

        if (searchQuery.isEmpty()) {
            showAlert("Warning", "Пожалуйста, введите имя пользователя для поиска");
            return;
        }
        Connect.client.sendMessage("searchUserByUsername");
        Connect.client.sendMessage(searchQuery);

        Object response = Connect.client.readObject();
        if ("OK".equals(response)) {
            try {
                List<User> users = (List<User>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<User> userData = FXCollections.observableArrayList(users);
                    infoTable.setItems(userData);
                });
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Не удалось выполнить поиск пользователей: неверный ответ от сервера");
            }
        } else if (response instanceof String) {
            showAlert("Error", (String) response);
        } else {
            showAlert("Error", "Неожиданный ответ от сервера");
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = infoTable.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            try {
                Connect.client.sendMessage("deleteUser");
                Connect.client.sendMessage(String.valueOf(selectedUser.getId()));

                String response = (String) Connect.client.readObject();
                if ("OK".equals(response)) {
                    showAlert("Success", "Пользователь успешно удален");
                    loadUsersFromServer();
                } else {
                    showAlert("Error", response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "При удалении пользователя произошла ошибка: " + e.getMessage());
            }
        } else {
            showAlert("Warning", "Пожалуйста, выберите пользователя для удаления");
        }
    }
}

