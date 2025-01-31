package runServer;

import dao.*;
import runServer.RequestHandler.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RequestFacade {
    private final UserDAO userDAO;
    private final PassengerDAO passengerDAO;
    private final AircraftDAO aircraftDAO;
    private final FlightDAO flightDAO;
    private final TicketDAO ticketDAO;

    public RequestFacade() {
        this.userDAO = new UserDAO();
        this.passengerDAO = new PassengerDAO();
        this.aircraftDAO = new AircraftDAO();
        this.flightDAO = new FlightDAO();
        this.ticketDAO = new TicketDAO();
    }

    public void processRequest(String command, ObjectInputStream sois, ObjectOutputStream soos) {
        try {
            switch (command) {
                case "authorization":
                    UserHandler.handleAuthorization(sois, soos, userDAO, passengerDAO);
                    break;
                case "buyTicket":
                    TicketHandler.handleBuyTicket(sois, soos, ticketDAO, passengerDAO);
                    break;
                case "cancelTicket":
                    TicketHandler.handleCancelTicket(sois, soos, ticketDAO, passengerDAO);
                    break;
                case "checkSeats":
                    TicketHandler.handleCheckSeats(sois, soos, ticketDAO);
                    break;
                case "generateReport":
                    FlightHandler.handleGenerateReport(sois, soos, flightDAO);
                    break;
                case "generateTicketReport":
                    TicketHandler.handleGenerateTicketReport(sois, soos, ticketDAO);
                    break;
                case "getUserData":
                    UserHandler.handleGetUserData(sois, soos, userDAO, passengerDAO);
                    break;
                case "getUserBalance":
                    UserHandler.handleGetUserBalance(sois, soos, userDAO);
                    break;
                case "getUserTickets":
                    TicketHandler.handleGetUserTickets(sois, soos, ticketDAO, passengerDAO, flightDAO);
                    break;
                case "registrationUser":
                    UserHandler.handleRegistrationUser(sois, soos, userDAO);
                    break;
                case "registrationPassenger":
                    PassengerHandler.handleRegistrationPassenger(sois, soos, passengerDAO);
                    break;
                case "addAircraft":
                    AircraftHandler.handleAddAircraft(sois, soos, aircraftDAO);
                    break;
                case "deleteAircraft":
                    AircraftHandler.handleDeleteAircraft(sois, soos, aircraftDAO);
                    break;
                case "updateAircraft":
                    AircraftHandler.handleUpdateAircraft(sois, soos, aircraftDAO);
                    break;
                case "getOnlineTableData":
                    FlightHandler.handleGetOnlineTableData(sois, soos, flightDAO);
                    break;
                case "getAllAircrafts":
                    AircraftHandler.handleGetAllAircrafts(sois, soos, aircraftDAO);
                    break;
                case "getAllTickets":
                    TicketHandler.handleGetAllTickets(sois, soos, ticketDAO);
                    break;
                case "searchAircraftByName":
                    AircraftHandler.handleSearchAircraftByName(sois, soos, aircraftDAO);
                    break;
                case "getAllUsers":
                    UserHandler.handleGetAllUsers(sois, soos, userDAO);
                    break;
                case "addUser":
                    UserHandler.handleAddUser(sois, soos, userDAO);
                    break;
                case "deleteUser":
                    UserHandler.handleDeleteUser(sois, soos, userDAO);
                    break;
                case "updateUser":
                    UserHandler.handleUpdateUser(sois, soos, userDAO);
                    break;
                case "addBalance":
                    UserHandler.handleAddBalance(sois, soos, userDAO);
                    break;
                case "getAllPassengers":
                    PassengerHandler.handleGetAllPassengers(sois, soos, passengerDAO);
                    break;
                case "searchUserByUsername":
                    UserHandler.handleSearchUserByUsername(sois, soos, userDAO);
                    break;
                case "deletePassenger":
                    PassengerHandler.handleDeletePassenger(sois, soos, passengerDAO);
                    break;
                case "updatePassenger":
                    PassengerHandler.handleUpdatePassenger(sois, soos, passengerDAO);
                    break;
                case "searchPassengerBySurname":
                    PassengerHandler.handleSearchPassengerBySurname(sois, soos, passengerDAO);
                    break;
                case "updatePassengerAndUser":
                    PassengerHandler.handleUpdatePassengerAndUser(sois, soos, passengerDAO, userDAO);
                    break;
                case "changePassword":
                    UserHandler.handleChangePassword(sois, soos, userDAO);
                    break;
                case "checkPassengerRegistration":
                    PassengerHandler.handleCheckPassengerRegistration(sois, soos, passengerDAO);
                    break;
                case "getAllFlights":
                    FlightHandler.handleGetAllFlights(sois, soos, flightDAO);
                    break;
                case "getAllFlightsForBuy":
                    FlightHandler.handleGetAllFlightsForBuy(sois, soos, flightDAO);
                    break;
                case "addFlight":
                    FlightHandler.handleAddFlight(sois, soos, flightDAO);
                    break;
                case "getAircraftNames":
                    AircraftHandler.handleGetAircraftNames(sois, soos, aircraftDAO);
                    break;
                case "getAircraftByName":
                    AircraftHandler.handleGetAircraftByName(sois, soos, aircraftDAO);
                    break;
                case "deleteFlight":
                    FlightHandler.handleDeleteFlight(sois, soos, flightDAO);
                    break;
                case "searchFlightByNumber":
                    FlightHandler.handleSearchFlightByNumber(sois, soos, flightDAO);
                    break;
                case "updateFlight":
                    FlightHandler.handleUpdateFlight(sois, soos, flightDAO);
                    break;
                case "getNameAircraftById":
                    AircraftHandler.handleGetNameAircraftById(sois, soos, aircraftDAO);
                    break;
                default:
                    System.out.println("Неизвестная команда: " + command);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при обработке команды: " + command + ", сообщение: " + e.getMessage());
        }
    }
}

