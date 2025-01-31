package dao;

import EnumPackage.TicketClass;
import Pojo.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO implements ITicketDAO {

    private DBConnection dbConnection;

    public TicketDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public boolean createTicket(Ticket ticket) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO tickets (flightID, passengerID, seats, class, price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, ticket.getFlightID());
            stmt.setInt(2, ticket.getPassengerID());
            stmt.setString(3, ticket.getSeats());
            stmt.setString(4, ticket.getTicketClass().toString());
            stmt.setBigDecimal(5, ticket.getPrice());
            stmt.setString(6, ticket.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Ошибка при создании билета", e);
        }
    }

    @Override
    public List<String> getOccupiedSeats(int flightID) {
        List<String> seats = new ArrayList<>();
        String query = "SELECT seats FROM tickets WHERE flightID = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, flightID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(rs.getString("seats"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return seats;
    }

    @Override
    public List<Ticket> getTicketsByUserId(int userId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE passengerID = ?";

        try (Connection connection = dbConnection.getDbConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(resultSet.getInt("id"));
                ticket.setFlightID(resultSet.getInt("flightID"));
                ticket.setSeats(resultSet.getString("seats"));
                ticket.setTicketClass(TicketClass.valueOf(resultSet.getString("class")));
                ticket.setPrice(resultSet.getBigDecimal("price"));
                ticket.setStatus(resultSet.getString("status"));
                tickets.add(ticket);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return tickets;
    }

    @Override
    public Ticket getTicketById(int ticketId) {
        String query = "SELECT id, flightID, passengerID, seats, class, price, status FROM tickets WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, ticketId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Ticket(
                            rs.getInt("id"),
                            rs.getInt("flightID"),
                            rs.getInt("passengerID"),
                            rs.getString("seats"),
                            TicketClass.valueOf(rs.getString("class").toUpperCase()),
                            rs.getBigDecimal("price"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении билета: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateTicketStatus(int ticketId, String status) {
        String query = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, ticketId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при обновлении статуса билета: " + e.getMessage());
        }
    }

    @Override
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT id, flightID, passengerID, seats, class, price, status FROM tickets";
        try (Connection connection = dbConnection.getDbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getInt("flightID"),
                        rs.getInt("passengerID"),
                        rs.getString("seats"),
                        TicketClass.valueOf(rs.getString("class").toUpperCase()),
                        rs.getBigDecimal("price"),
                        rs.getString("status")
                );
                tickets.add(ticket);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка при получении билетов: " + e.getMessage());
        }
        return tickets;
    }
}
