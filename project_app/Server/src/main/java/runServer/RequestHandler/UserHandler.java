package runServer.RequestHandler;

import Pojo.*;
import HashPassword.PasswordUtils;
import dao.FlightDAO;
import dao.PassengerDAO;
import dao.TicketDAO;
import dao.UserDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class UserHandler {

    public static void handleAuthorization(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO, PassengerDAO passengerDAO) {
        try {
            System.out.println("Выполняется авторизация пользователя...");
            Authorization auth = (Authorization) sois.readObject();
            User authorizedUser = userDAO.authenticateUser(auth.getUsername(), auth.getPassword());

            if (authorizedUser != null) {
                soos.writeObject("OK");
                soos.writeObject(authorizedUser);

                Integer passengerId = passengerDAO.getPassengerIdByUserId(authorizedUser.getId());
                if (passengerId == null) {
                    System.out.println("Пассажир с userId=" + authorizedUser.getId() + " не найден.");
                    soos.writeObject("Passenger not found");
                } else {
                    soos.writeObject("Passenger found");
                }
            } else {
                soos.writeObject("Invalid credentials or user not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error during authorization: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGetUserBalance(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            String userIdString = (String) sois.readObject();
            System.out.println("Received userId: " + userIdString);
            Integer userId = Integer.parseInt(userIdString);
            BigDecimal balance = userDAO.getUserBalanceById(userId);

            if (balance != null) {
                soos.writeObject("OK");
                soos.writeObject(balance.toString());
            } else {
                soos.writeObject("Баланс пользователя не найден");
            }
        } catch (Exception e) {
            handleException(e, soos, "Ошибка получения баланса пользователя");
        }
    }

    public static void handleAddBalance(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            String userIdString = (String) sois.readObject();
            System.out.println("Received userId: " + userIdString);
            Integer userId = Integer.parseInt(userIdString);

            String sumString = (String) sois.readObject();
            System.out.println("Received sum: " + sumString);
            BigDecimal sum = new BigDecimal(sumString);

            if (userDAO.addBalance(userId, sum)) {
                soos.writeObject("OK");
                System.out.println("Баланс успешно пополнен.");
            } else {
                soos.writeObject("Error adding balance");
                System.err.println("Не удалось обновить баланс.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса на пополнение баланса: " + e.getMessage());
            e.printStackTrace();
            try {
                soos.writeObject("Error adding balance: " + e.getMessage());
            } catch (IOException ex) {
                System.err.println("Ошибка при отправке ответа клиенту: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    public static void handleGetUserData(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO, PassengerDAO passengerDAO) {
        try {
            int userId = (int) sois.readObject();
            User user = userDAO.getUserById(userId);
            Passenger passenger = passengerDAO.getPassengerByUserId(userId);

            if (user != null && passenger != null) {
                soos.writeObject("OK");
                soos.writeObject(passenger);

                String confirmation = (String) sois.readObject();
                if ("OK".equals(confirmation)) {
                    soos.writeObject(user.getUsername());
                }
            } else {
                soos.writeObject("Данные пользователя не найдены");
            }
        } catch (Exception e) {
            handleException(e, soos, "Ошибка получения данных пользователя");
        }
    }

    public static void handleRegistrationUser(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            User user = (User) sois.readObject();
            if (userDAO.isUserExists(user.getUsername())) {
                soos.writeObject("This user already exists");
            } else {
                User registeredUser = userDAO.registerUser(user);
                if (registeredUser != null) {
                    soos.writeObject("OK");
                    soos.writeObject(registeredUser);
                } else {
                    soos.writeObject("Error during registration");
                }
            }
        } catch (Exception e) {
            handleException(e, soos, "Error during registration");
        }
    }

    public static void handleGetAllUsers(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            List<User> users = userDAO.getAllUsers();
            if (users != null && !users.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(users);
            } else {
                soos.writeObject("No users found in the database.");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error retrieving users");
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

    public static void handleAddUser(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            User newUser = (User) sois.readObject();
            if (userDAO.isUserExists(newUser.getUsername())) {
                soos.writeObject("Error: User with this username already exists.");
            } else {
                User registeredUser = userDAO.registerUser(newUser);
                if (registeredUser != null) {
                    soos.writeObject("OK");
                } else {
                    soos.writeObject("Error: Failed to register the user.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleDeleteUser(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            Object receivedObject = sois.readObject();
            int userId = (receivedObject instanceof Integer) ? (Integer) receivedObject : Integer.parseInt((String) receivedObject);
            if (userDAO.deleteUser(userId)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error: User not found or failed to delete.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error deleting user: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleUpdateUser(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            User updatedUser = (User) sois.readObject();
            if (userDAO.updateUser(updatedUser.getUsername(), updatedUser.getRole(), updatedUser.getId())) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error updating user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error updating user: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleSearchUserByUsername(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            String username = (String) sois.readObject();
            if (username == null || username.trim().isEmpty()) {
                soos.writeObject("Error: Search query cannot be empty.");
            } else {
                List<User> users = userDAO.searchUsersByUsername(username);
                if (users != null && !users.isEmpty()) {
                    soos.writeObject("OK");
                    soos.writeObject(users);
                } else {
                    soos.writeObject("No users found matching the search query.");
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error searching for users: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleChangePassword(ObjectInputStream sois, ObjectOutputStream soos, UserDAO userDAO) {
        try {
            int userId = (int) sois.readObject();
            String currentPassword = (String) sois.readObject();
            String newPassword = (String) sois.readObject();

            User user = userDAO.getUserById(userId);
            if (user == null || !PasswordUtils.hashPassword(currentPassword).equals(user.getPassword())) {
                soos.writeObject("Invalid current password");
                return;
            }

            String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
            if (userDAO.updatePassword(userId, hashedNewPassword)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error updating password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error changing password: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
