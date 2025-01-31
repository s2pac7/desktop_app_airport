package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private final String dbHost = "localhost";
    private final String dbPort = "3306";
    private final String dbName = "airportauto";
    private final String dbUser = "root";
    private final String dbPass = "root";

    public  DBConnection() {}

    // Получение единственного экземпляра DBConnection (Singleton)
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public  Connection getDbConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
