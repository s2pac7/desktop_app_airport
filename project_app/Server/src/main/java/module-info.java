module com.example.server {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;
        requires jakarta.xml.bind;
        requires com.google.gson;

        exports dao;
        exports ReceiptGenerator;
        exports runServer;
        exports Pojo;
        opens Pojo to jakarta.xml.bind;
        opens dao to org.junit.jupiter.api;
}

//module com.example.server {
//        requires javafx.controls;
//        requires javafx.fxml;
//        requires java.sql;
//        requires jakarta.xml.bind;
//        requires com.google.gson;
//
//        exports runServer;
//        exports Pojo;
//        opens Pojo to jakarta.xml.bind;
//        }