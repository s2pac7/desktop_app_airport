package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import runClient.Client;
import runClient.Connect;

import java.io.IOException;

public class Main extends Application {

    // Точка входа для графического интерфейса
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/autorization.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1211, 700);
        stage.setTitle("Authorization");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        Connect.client = new Client("127.0.0.1", "1337");
        launch();
    }
}