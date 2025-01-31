package controllers;

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

public class EditAircraftController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private Button editAircraftButton;

    @FXML
    private TextField editAircraftNameField;

    @FXML
    private TextField editAircraftTypeField;

    private Aircraft selectedAircraft;

    @FXML
    void initialize() {
        backButton.setOnAction(event -> closeWindow());
        editAircraftButton.setOnAction(event -> updateAircraft());
    }

    public void setAircraftData(Aircraft aircraft) {
        this.selectedAircraft = aircraft;
        editAircraftNameField.setText(aircraft.getAircraftName());
        editAircraftTypeField.setText(aircraft.getAircraftType());
    }

    private void updateAircraft() {
        if (editAircraftNameField.getText().isEmpty() || editAircraftTypeField.getText().isEmpty()) {
            showAlert("Error", "Все поля должны быть заполнены");
            return;
        }

        selectedAircraft.setAircraftName(editAircraftNameField.getText());
        selectedAircraft.setAircraftType(editAircraftTypeField.getText());

        Connect.client.sendMessage("updateAircraft");
        Connect.client.sendObject(selectedAircraft);

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            showAlert("Success", "Самолет успешно обновлен");
            closeWindow();
        } else {
            showAlert("Error", "Не удалось обновить самолет");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
