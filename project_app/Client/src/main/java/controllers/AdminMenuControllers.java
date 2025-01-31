package controllers;

import Session.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuControllers {

    @FXML
    private Button aircraftsButton;

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button flightsButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button passengersButton;

    @FXML
    private Button soldTicketsButton;

    @FXML
    private Button usersButton;

    @FXML
    private void initialize() {
        myInfoButton.setOnAction(event -> {
            try {
                goToAuthorization(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        setButtonAction(aircraftsButton, "/aircrafts.fxml");
        setButtonAction(backToMenuButton, "/autorization.fxml");
        setButtonAction(flightsButton, "/flights.fxml");
        setButtonAction(passengersButton, "/passengers.fxml");
        setButtonAction(soldTicketsButton, "/tickets.fxml");
        setButtonAction(usersButton, "/users.fxml");
    }

    private void goToAuthorization(ActionEvent event) throws IOException {
        int currentUserId = SessionData.getCurrentUserId();

        if (currentUserId == -1) {
            System.out.println("Ошибка: currentUserId не установлен");
            return;
        }

        Stage currentStage = (Stage) myInfoButton.getScene().getWindow();
        currentStage.hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/myInfoAdmin.fxml"));
        Parent root = loader.load();

        AdminMyInfoControllers controller = loader.getController();
        controller.setCurrentUserId(currentUserId);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setButtonAction(Button button, String fxmlPath) {
        button.setOnAction(event -> {
            try {
                loadScene(event, fxmlPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка при загрузке сцены: " + fxmlPath);
            }
        });
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        System.out.println("Загружаем сцену: " + fxmlPath);

        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        if (fxmlPath.equals("/autorization.fxml")) {
            SessionData.clearSession(); // Сброс данных о пользователе при выходе
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
