package dao;

import Pojo.Aircraft;
import Pojo.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlightDAO implements IFlightDAO {
    private DBConnection dbConnection;

    public FlightDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String query = """
                SELECT f.id, COALESCE(a.nameAircraft, 'Unknown') AS nameAircraft,
                       f.flightNumber, f.departureAirport, f.arrivalAirport,
                       f.departureTime, f.arrivalTime
                FROM flights f
                LEFT JOIN aircrafts a ON f.aircraftID = a.id
                """;

        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LocalDateTime departureTime = rs.getTimestamp("departureTime") != null
                        ? rs.getTimestamp("departureTime").toLocalDateTime()
                        : null;
                LocalDateTime arrivalTime = rs.getTimestamp("arrivalTime") != null
                        ? rs.getTimestamp("arrivalTime").toLocalDateTime()
                        : null;

                Flight flight = new Flight(
                        rs.getInt("id"),
                        rs.getString("nameAircraft"),
                        rs.getString("flightNumber"),
                        rs.getString("departureAirport"),
                        rs.getString("arrivalAirport"),
                        departureTime,
                        arrivalTime
                );
                flights.add(flight);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении рейсов: " + e.getMessage());
        }
        return flights;
    }

    @Override
    public List<Object> getOnlineTableData() {
        List<Object> results = new ArrayList<>();
        List<Flight> flights = new ArrayList<>();
        List<Aircraft> aircrafts = new ArrayList<>();

        String query = """
                    SELECT 
                        f.flightNumber, 
                        f.departureAirport, 
                        f.arrivalAirport, 
                        f.departureTime, 
                        f.arrivalTime,
                        a.nameAircraft, 
                        a.typeAircraft
                    FROM flights f
                    LEFT JOIN aircrafts a ON f.aircraftID = a.id
                """;

        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Flight flight = new Flight(
                        rs.getString("flightNumber"),
                        rs.getString("departureAirport"),
                        rs.getString("arrivalAirport"),
                        rs.getTimestamp("departureTime").toLocalDateTime(),
                        rs.getTimestamp("arrivalTime").toLocalDateTime()
                );
                flights.add(flight);

                Aircraft aircraft = new Aircraft(
                        rs.getString("nameAircraft"),
                        rs.getString("typeAircraft")
                );
                aircrafts.add(aircraft);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении данных для онлайн-табло: " + e.getMessage());
        }

        results.add(flights);
        results.add(aircrafts);
        return results;
    }

    @Override
    public List<Flight> getAllFlightsForBuy() {
        List<Flight> flights = new ArrayList<>();
        String query = """
                SELECT f.id, a.nameAircraft,
                       f.flightNumber, f.departureAirport, f.arrivalAirport,
                       f.departureTime, f.arrivalTime
                FROM flights f
                LEFT JOIN aircrafts a ON f.aircraftID = a.id
                WHERE f.departureAirport = "Aviasales"
                """;

        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LocalDateTime departureTime = rs.getTimestamp("departureTime") != null
                        ? rs.getTimestamp("departureTime").toLocalDateTime()
                        : null;
                LocalDateTime arrivalTime = rs.getTimestamp("arrivalTime") != null
                        ? rs.getTimestamp("arrivalTime").toLocalDateTime()
                        : null;

                Flight flight = new Flight(
                        rs.getInt("id"),
                        rs.getString("nameAircraft"),
                        rs.getString("flightNumber"),
                        rs.getString("departureAirport"),
                        rs.getString("arrivalAirport"),
                        departureTime,
                        arrivalTime
                );
                flights.add(flight);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении рейсов: " + e.getMessage());
        }
        return flights;
    }

    @Override
    public boolean addFlight(Flight flight) throws SQLException {
        String query = "INSERT INTO flights (aircraftID, flightNumber, departureAirport, arrivalAirport, departureTime, arrivalTime) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, flight.getAircraftID());
            stmt.setString(2, flight.getFlightNumber());
            stmt.setString(3, flight.getDepartureAirport());
            stmt.setString(4, flight.getArrivalAirport());
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFlightById(int flightId) {
        String query = "DELETE FROM flights WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, flightId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Flight> searchFlightByNumber(String flightNumber) {
        List<Flight> flights = new ArrayList<>();
        String query = """
                    SELECT f.id, a.nameAircraft, f.flightNumber, 
                           f.departureAirport, f.arrivalAirport, 
                           f.departureTime, f.arrivalTime
                    FROM flights f
                    LEFT JOIN aircrafts a ON f.aircraftID = a.id
                    WHERE f.flightNumber LIKE ?
                """;
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + flightNumber + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight(
                            rs.getInt("id"),
                            rs.getString("nameAircraft"),
                            rs.getString("flightNumber"),
                            rs.getString("departureAirport"),
                            rs.getString("arrivalAirport"),
                            rs.getTimestamp("departureTime").toLocalDateTime(),
                            rs.getTimestamp("arrivalTime").toLocalDateTime()
                    );
                    flights.add(flight);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return flights;
    }

    @Override
    public boolean updateFlight(Flight flight) {
        String query = "UPDATE flights SET flightNumber = ?, departureAirport = ?, arrivalAirport = ?, " +
                "departureTime = ?, arrivalTime = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getDepartureAirport());
            stmt.setString(3, flight.getArrivalAirport());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(6, flight.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Flight> getFlightsByTicketIds(List<Integer> flightIds) throws SQLException {
        if (flightIds == null || flightIds.isEmpty()) {
            return Collections.emptyList();         }

        StringBuilder query = new StringBuilder("SELECT * FROM flights WHERE id IN (");
        query.append(String.join(",", Collections.nCopies(flightIds.size(), "?"))).append(")");

        List<Flight> flights = new ArrayList<>();
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < flightIds.size(); i++) {
                stmt.setInt(i + 1, flightIds.get(i));
            }

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Flight flight = new Flight();
                flight.setId(resultSet.getInt("id"));
                flight.setFlightNumber(resultSet.getString("flightNumber"));
                flight.setDepartureAirport(resultSet.getString("departureAirport"));
                flight.setArrivalAirport(resultSet.getString("arrivalAirport"));
                flight.setDepartureTime(resultSet.getTimestamp("departureTime").toLocalDateTime());
                flight.setArrivalTime(resultSet.getTimestamp("arrivalTime").toLocalDateTime());
                flights.add(flight);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return flights;
    }
}
