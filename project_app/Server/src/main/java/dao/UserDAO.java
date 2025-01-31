package dao;

import Pojo.User;
import HashPassword.PasswordUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private DBConnection dbConnection;

    public UserDAO() {
        this.dbConnection = new DBConnection();
    }

    @Override
    public boolean addBalance(int userId, BigDecimal sum) {
        String query = "UPDATE passengers SET balance = balance + ? WHERE userID = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, sum);
            stmt.setInt(2, userId);

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

            return rowsUpdated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при обновлении баланса: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BigDecimal getUserBalanceById(int userId) {
        String query = "SELECT balance FROM passengers WHERE userID = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка при получении баланса: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isUserExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection();
        }
        return false;
    }

    @Override
    public User registerUser(User user) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            String hashedPassword = PasswordUtils.hashPassword(user.getPassword());

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getRole());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return user;
            }
        } finally {
            dbConnection.closeConnection();
        }
        return null;
    }

    @Override
    public User authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String hashedPasswordFromDb = resultSet.getString("password");
                String hashedInputPassword = PasswordUtils.hashPassword(password);
                if (hashedPasswordFromDb.equals(hashedInputPassword)) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                    return user;
                }
            }
        } finally {
            dbConnection.closeConnection();
        }
        return null;
    }


    @Override
    public User getUserById(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return users;
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public boolean updateUser(String username, String role, int userId) throws SQLException {
        String query = "UPDATE users SET username = ?, role = ? WHERE id = ?";

        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public boolean updateUsername(int userId, String newUsername) throws SQLException {
        String query = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public List<User> searchUsersByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + username + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return users;
    }

    @Override
    public boolean updatePassword(int userId, String hashedPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }
}





