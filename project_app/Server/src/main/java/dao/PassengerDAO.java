package dao;

import Pojo.Passenger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO implements IPassengerDAO {
    private DBConnection dbConnection;

    public PassengerDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public Passenger getPassengerByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM passengers WHERE userId = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(rs.getInt("id"));
                passenger.setUserId(rs.getInt("userId"));
                passenger.setName(rs.getString("name"));
                passenger.setSurname(rs.getString("surname"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setDateOfBirth(rs.getString("dateOfBirth"));
                passenger.setPhoneNumber(rs.getString("phoneNumber"));
                passenger.setBalance(rs.getBigDecimal("balance"));
                return passenger;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Ошибка при получении пассажира по userId", e);
        }
        return null;
    }

    @Override
    public Integer getPassengerIdByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT id FROM passengers WHERE userId = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Ошибка при получении passengerId по userId", e);
        }
        return null;
    }

    @Override
    public Passenger registerPassenger(Passenger passenger) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO passengers (userId, name, surname, passportNumber, dateOfBirth, phoneNumber, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, passenger.getUserId());
            stmt.setString(2, passenger.getName());
            stmt.setString(3, passenger.getSurname());
            stmt.setString(4, passenger.getPassportNumber());
            stmt.setString(5, passenger.getDateOfBirth());
            stmt.setString(6, passenger.getPhoneNumber());
            stmt.setBigDecimal(7, passenger.getBalance());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    passenger.setId(generatedKeys.getInt(1));
                }
                return passenger;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Ошибка при регистрации пассажира", e);
        }
        return null;
    }

    @Override
    public boolean isPassengerExists(String passportNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM passengers WHERE passportNumber = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, passportNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return false;
    }

    @Override
    public List<Passenger> getAllPassengers() throws SQLException {
        String query = "SELECT * FROM passengers";
        List<Passenger> passengers = new ArrayList<>();
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(rs.getInt("id"));
                passenger.setUserId(rs.getInt("userId"));
                passenger.setName(rs.getString("name"));
                passenger.setSurname(rs.getString("surname"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setDateOfBirth(rs.getString("dateOfBirth"));
                passenger.setPhoneNumber(rs.getString("phoneNumber"));
                passenger.setBalance(rs.getBigDecimal("balance"));
                passengers.add(passenger);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return passengers;
    }

    @Override
    public boolean updatePassenger(Passenger passenger) throws SQLException {
        String query = "UPDATE passengers SET name = ?, surname = ?, passportNumber = ?, dateOfBirth = ?, phoneNumber = ?, balance = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, passenger.getName());
            stmt.setString(2, passenger.getSurname());
            stmt.setString(3, passenger.getPassportNumber());
            stmt.setString(4, passenger.getDateOfBirth());
            stmt.setString(5, passenger.getPhoneNumber());
            stmt.setBigDecimal(6, passenger.getBalance());
            stmt.setInt(7, passenger.getId());

            return stmt.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deletePassenger(int passengerId) throws SQLException {
        String query = "DELETE FROM passengers WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, passengerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public List<Passenger> searchPassengersBySurname(String surname) throws SQLException {
        String query = "SELECT * FROM passengers WHERE surname LIKE ?";
        List<Passenger> passengers = new ArrayList<>();
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + surname + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(rs.getInt("id"));
                passenger.setUserId(rs.getInt("userId"));
                passenger.setName(rs.getString("name"));
                passenger.setSurname(rs.getString("surname"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setDateOfBirth(rs.getString("dateOfBirth"));
                passenger.setPhoneNumber(rs.getString("phoneNumber"));
                passenger.setBalance(rs.getBigDecimal("balance"));
                passengers.add(passenger);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return passengers;
    }

    @Override
    public boolean isPassengerRegistered(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM passengers WHERE userId = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return false;
    }

    @Override
    public Passenger getPassengerById(int passengerId) throws SQLException {
        String query = "SELECT * FROM passengers WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(rs.getInt("id"));
                passenger.setUserId(rs.getInt("userId"));
                passenger.setName(rs.getString("name"));
                passenger.setSurname(rs.getString("surname"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setDateOfBirth(rs.getString("dateOfBirth"));
                passenger.setPhoneNumber(rs.getString("phoneNumber"));
                passenger.setBalance(rs.getBigDecimal("balance"));

                return passenger;
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public boolean addBalance(int passengerId, BigDecimal amount) {
        String query = "UPDATE passengers SET balance = balance + ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, passengerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при пополнении счета: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePassengerBalance(Passenger passenger) throws SQLException {
        String query = "UPDATE passengers SET balance = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setBigDecimal(1, passenger.getBalance());
            stmt.setInt(2, passenger.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }
}

