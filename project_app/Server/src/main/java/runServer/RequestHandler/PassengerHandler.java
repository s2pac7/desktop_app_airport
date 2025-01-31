package runServer.RequestHandler;

import Pojo.Passenger;
import dao.PassengerDAO;
import dao.UserDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;

public class PassengerHandler {

    public static void handleRegistrationPassenger(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            Passenger passenger = (Passenger) sois.readObject();
            Passenger registeredPassenger = passengerDAO.registerPassenger(passenger);

            if (registeredPassenger != null) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error during passenger registration");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error during passenger registration");
        }
    }

    private static void handleException(Exception e, ObjectOutputStream soos, String errorMessage) {
        e.printStackTrace();
        try {
            soos.writeObject(errorMessage + ": " + e.getMessage());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void handleGetAllPassengers(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            List<Passenger> passengers = passengerDAO.getAllPassengers();

            if (passengers != null && !passengers.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(passengers);
            } else {
                soos.writeObject("No passengers found in the database.");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error retrieving passengers");
        }
    }

    public static void handleSearchPassengerBySurname(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            String surnameQuery = (String) sois.readObject();

            if (surnameQuery == null || surnameQuery.trim().isEmpty()) {
                soos.writeObject("Error: Search query cannot be empty.");
                return;
            }

            List<Passenger> passengers = passengerDAO.searchPassengersBySurname(surnameQuery);

            if (!passengers.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(passengers);
            } else {
                soos.writeObject("Ошибка: Пассажиры не найдены.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Ошибка при поиске пассажира: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleDeletePassenger(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            Object receivedObject = sois.readObject();
            int passengerId = (receivedObject instanceof Integer) ? (Integer) receivedObject : Integer.parseInt((String) receivedObject);
            if (passengerDAO.deletePassenger(passengerId)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error: Passenger not found or failed to delete.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error deleting passenger: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleCheckPassengerRegistration(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            int userId = (int) sois.readObject();
            boolean isRegistered = passengerDAO.isPassengerRegistered(userId);

            if (isRegistered) {
                soos.writeObject("REGISTERED");
            } else {
                soos.writeObject("NOT_REGISTERED");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleUpdatePassenger(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO) {
        try {
            Passenger updatedPassenger = (Passenger) sois.readObject();

            if (passengerDAO.updatePassenger(updatedPassenger)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Ошибка при обновлении пассажира.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Ошибка при обновлении пассажира: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleUpdatePassengerAndUser(ObjectInputStream sois, ObjectOutputStream soos, PassengerDAO passengerDAO, UserDAO userDAO) {
        try {
            Passenger updatedPassenger = (Passenger) sois.readObject();
            String newUsername = (String) sois.readObject();

            boolean isUpdated = false;

            if (passengerDAO.updatePassenger(updatedPassenger)) {
                isUpdated = true;
            }

            if (newUsername != null && !newUsername.isEmpty()) {
                if (userDAO.updateUsername(updatedPassenger.getUserId(), newUsername)) {
                    isUpdated = true;
                }
            }
            if (isUpdated) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("No changes were made");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error updating passenger and user: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

