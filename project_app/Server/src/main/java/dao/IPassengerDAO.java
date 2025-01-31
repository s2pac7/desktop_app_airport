package dao;

import Pojo.Passenger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IPassengerDAO {
    Passenger getPassengerByUserId(int userId) throws SQLException, ClassNotFoundException;

    Passenger registerPassenger(Passenger passenger) throws SQLException, ClassNotFoundException;

    boolean isPassengerExists(String passportNumber) throws SQLException;

    List<Passenger> getAllPassengers() throws SQLException;

    boolean updatePassenger(Passenger passenger) throws SQLException;

    boolean deletePassenger(int passengerId) throws SQLException;

    List<Passenger> searchPassengersBySurname(String surname) throws SQLException;

    boolean isPassengerRegistered(int userId) throws SQLException;

    Integer getPassengerIdByUserId(int userId) throws SQLException, ClassNotFoundException;

    boolean addBalance(int passengerId, BigDecimal amount);

    boolean updatePassengerBalance(Passenger passenger) throws SQLException;

    Passenger getPassengerById(int passengerId) throws SQLException;
}
