package controllers;

import Patterns.builders.AircraftBuilder;
import check.Dialog;
import runClient.Connect;
import Pojo.Aircraft;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static check.Dialog.showAlert;

public class AddAircraftController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addAircraftButton;

    @FXML
    private TextField addAircraftNameField;

    @FXML
    private TextField addAircraftTypeField;

    @FXML
    private Button backButton;

    @FXML
    void initialize() {
        addAircraftButton.setOnAction(event -> addAircraft());
        backButton.setOnAction(event -> closeWindow());
    }

    private void addAircraft() {
        String aircraftName = addAircraftNameField.getText();
        String aircraftType = addAircraftTypeField.getText();

        if (aircraftName.isEmpty() || aircraftType.isEmpty()) {
            showAlert("Error", "Все поля должны быть заполнены");
            return;
        }
        try {
            Aircraft newAircraft = new AircraftBuilder()
                    .setId(0)
                    .setName(aircraftName)
                    .setType(aircraftType)
                    .build();

            Connect.client.sendMessage("addAircraft");
            Connect.client.sendObject(newAircraft);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                showAlert("Success", "Самолет успешно добавлен");
                closeWindow();
            } else {
                showAlert("Error", "Не удалось добавить самолет");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

}
