package controllers;

import Pojo.Ticket;
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
import runClient.Connect;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static check.Dialog.showAlert;

public class PageTicketsAdmin {

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createReportButton;

    @FXML
    private TableColumn<Ticket, String> classSeatColumn;

    @FXML
    private TableColumn<Ticket, Integer> flightIdColumn;

    @FXML
    private TableView<Ticket> infoTableTicket;

    @FXML
    private TableColumn<Ticket, Integer> passengerIdColumn;

    @FXML
    private TableColumn<Ticket, BigDecimal> priceColumn;

    @FXML
    private TableColumn<Ticket, String> seatsColumn;

    @FXML
    private Button showButton;

    @FXML
    private TableColumn<Ticket, String> statusColumn;

    @FXML
    private TableColumn<Ticket, String> ticketIdColumn;

    @FXML
    void initialize() {
        setupTableColumns();

        showButton.setOnAction(event -> loadTicketsFromServer());
        backToMenuButton.setOnAction(event -> backToMenu());
        cancelButton.setOnAction(event -> cancelTicket());
        createReportButton.setOnAction(event -> generateReport());
    }

    private void setupTableColumns() {
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        flightIdColumn.setCellValueFactory(new PropertyValueFactory<>("flightID"));
        passengerIdColumn.setCellValueFactory(new PropertyValueFactory<>("passengerID"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        classSeatColumn.setCellValueFactory(new PropertyValueFactory<>("ticketClass"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTicketsFromServer() {
        Connect.client.sendMessage("getAllTickets");
        String response = (String) Connect.client.readObject();

        if ("OK".equals(response)) {
            try {
                List<Ticket> tickets = (List<Ticket>) Connect.client.readObject();
                Platform.runLater(() -> {
                    ObservableList<Ticket> ticketData = FXCollections.observableArrayList(tickets);
                    infoTableTicket.setItems(ticketData);
                });
            } catch (ClassCastException e) {
                showAlert("Error", "Не удалось загрузить билеты: неверный ответ от сервера");
            }
        } else {
            showAlert("Error", response);
        }
    }

    private void generateReport() {
        try {
            String fileName = "ticket_report.txt";
            Connect.client.sendMessage("generateTicketReport");
            Connect.client.sendMessage(fileName);

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                byte[] fileBytes = (byte[]) Connect.client.readObject();

                // Создаем путь для сохранения отчета относительно текущей директории
                String reportsDir = System.getProperty("user.dir") + File.separator + "Reports";
                Path reportPath = Paths.get(reportsDir, fileName);
                Files.createDirectories(reportPath.getParent()); // Убедиться, что директория существует
                Files.write(reportPath, fileBytes);

                showAlert("Success", "Отчет успешно сохранен: " + reportPath.toAbsolutePath());
            } else {
                showAlert("Error", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Не удалось создать отчет");
        }
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

    private void cancelTicket() {
        Ticket selectedTicket = infoTableTicket.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            showAlert("Error", "Пожалуйста, выберите билет для отмены");
            return;
        }

        Connect.client.sendMessage("cancelTicket");
        Connect.client.sendMessage(selectedTicket.getId().toString());

        String response = (String) Connect.client.readObject();
        if ("OK".equals(response)) {
            showAlert("Success", "Билет отменен, деньги успешно возвращены");
            loadTicketsFromServer();
        } else {
            showAlert("Error", response);
        }
    }
}
