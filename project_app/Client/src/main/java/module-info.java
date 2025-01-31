module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;

    opens main to javafx.fxml;
    exports main;
    exports controllers;
    opens controllers to javafx.fxml;
    opens Pojo to javafx.base;

}