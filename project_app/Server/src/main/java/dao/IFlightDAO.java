package dao;

import Pojo.Flight;

import java.sql.SQLException;
import java.util.List;

public interface IFlightDAO {
    List<Flight> getAllFlights();

    boolean addFlight(Flight flight) throws SQLException;

    boolean deleteFlightById(int flightId);

    List<Flight> searchFlightByNumber(String flightNumber);

    boolean updateFlight(Flight flight);

    List<Flight> getAllFlightsForBuy();

    List<Object> getOnlineTableData();

    List<Flight> getFlightsByTicketIds(List<Integer> flightIds) throws SQLException;
}
