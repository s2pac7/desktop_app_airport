package dao;

import Pojo.Ticket;

import java.sql.SQLException;
import java.util.List;

public interface ITicketDAO {

    boolean createTicket(Ticket ticket) throws SQLException, ClassNotFoundException;

    List<String> getOccupiedSeats(int flightID);

    List<Ticket> getTicketsByUserId(int userId) throws SQLException;

    Ticket getTicketById(int ticketId);

    boolean updateTicketStatus(int ticketId, String status);

    List<Ticket> getAllTickets();
}
