package dao;

import Pojo.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    boolean isUserExists(String username) throws SQLException;

    User registerUser(User user) throws SQLException, ClassNotFoundException;

    User authenticateUser(String username, String password) throws SQLException, ClassNotFoundException;

    User getUserById(int userId) throws SQLException, ClassNotFoundException;

    List<User> getAllUsers() throws SQLException;

    boolean deleteUser(int userId) throws SQLException;

    boolean updateUser(String username, String role, int userId) throws SQLException;

    boolean updateUsername(int userId, String newUsername) throws SQLException;

    List<User> searchUsersByUsername(String username) throws SQLException;

    boolean updatePassword(int userId, String hashedPassword) throws SQLException;

    BigDecimal getUserBalanceById(int userId);

    boolean addBalance(int userId, BigDecimal sum);
}
