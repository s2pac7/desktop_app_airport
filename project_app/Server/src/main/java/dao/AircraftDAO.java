package dao;

import Pojo.Aircraft;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AircraftDAO implements IAircraftDAO {
    private DBConnection dbConnection;

    public AircraftDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public void setDbConnection(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean addAircraft(Aircraft aircraft) throws SQLException {
        String query = "INSERT INTO aircrafts (nameAircraft, typeAircraft) VALUES (?, ?)";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, aircraft.getAircraftName());
            stmt.setString(2, aircraft.getAircraftType());
            return stmt.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAircraft(int id) {
        String query = "DELETE FROM aircrafts WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при удалении самолета из : " + e.getMessage());
        }
    }

    @Override
    public List<Aircraft> getAllAircrafts() {
        List<Aircraft> aircrafts = new ArrayList<>();
        String query = "SELECT id, nameAircraft, typeAircraft FROM aircrafts";
        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aircraft aircraft = new Aircraft(
                        rs.getInt("id"),
                        rs.getString("nameAircraft"),
                        rs.getString("typeAircraft")
                );
                aircrafts.add(aircraft);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении самолетов: " + e.getMessage());
        }
        return aircrafts;
    }

    @Override
    public boolean updateAircraft(Aircraft aircraft) throws SQLException {
        String query = "UPDATE aircrafts SET nameAircraft = ?, typeAircraft = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, aircraft.getAircraftName());
            stmt.setString(2, aircraft.getAircraftType());
            stmt.setInt(3, aircraft.getId());
            return stmt.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Aircraft> searchAircraftsByName(String searchQuery) throws SQLException {
        List<Aircraft> aircrafts = new ArrayList<>();
        String query = "SELECT * FROM aircrafts WHERE nameAircraft LIKE ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Aircraft aircraft = new Aircraft(
                        rs.getInt("id"),
                        rs.getString("nameAircraft"),
                        rs.getString("typeAircraft")
                );
                aircrafts.add(aircraft);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return aircrafts;
    }

    @Override
    public List<String> getAllAircraftNames() {
        List<String> names = new ArrayList<>();
        String query = "SELECT nameAircraft FROM aircrafts";

        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                names.add(rs.getString("nameAircraft"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return names;
    }

    @Override
    public Aircraft getAircraftByName(String name) {
        String query = "SELECT id, nameAircraft, typeAircraft FROM aircrafts WHERE nameAircraft = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Aircraft(
                        rs.getInt("id"),
                        rs.getString("nameAircraft"),
                        rs.getString("typeAircraft")
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при поиске самолета: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getAircraftNameById(int id) {
        String query = "SELECT nameAircraft FROM aircrafts WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nameAircraft");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при поиске самолета: " + e.getMessage());
        }
        return null;
    }
}
