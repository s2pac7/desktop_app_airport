package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UserMenuControllers {
    @FXML
    private Button backToMenuButton;

    @FXML
    private Button buyTicketButton;

    @FXML
    private Button myBasketButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button onlineTableButton;

    @FXML
    void initialize() {
        myInfoButton.setOnAction(event -> {
            try {
                goToMyInfo(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        backToMenuButton.setOnAction(event -> {
            try {
                goToBackToMenu(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buyTicketButton.setOnAction(event -> {
            try {
                goToBuyTicket(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        myBasketButton.setOnAction(event -> {
            try {
                goToMyBasket(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        onlineTableButton.setOnAction(event -> {
            try {
                goToOnlineTable(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void goToMyInfo(ActionEvent event) throws IOException {
        loadScene(event, "/myInfoUser.fxml");
    }

    private void goToBackToMenu(ActionEvent event) throws IOException {
        loadScene(event, "/autorization.fxml");
    }

    private void goToBuyTicket(ActionEvent event) throws IOException {
        loadScene(event, "/flightsUser.fxml");
    }

    private void goToMyBasket(ActionEvent event) throws IOException {
        loadScene(event, "/myBasket.fxml");
    }

    private void goToOnlineTable(ActionEvent event) throws IOException {
        loadScene(event, "/onlineTable.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
