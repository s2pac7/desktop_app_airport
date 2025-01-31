package runServer.RequestHandler;

import EnumPackage.TicketClass;
import Pojo.Flight;
import Pojo.Passenger;
import Pojo.Ticket;
import dao.FlightDAO;
import dao.PassengerDAO;
import dao.TicketDAO;

import java.io.*;
import ReceiptGenerator.ReceiptGenerator;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
public class TicketHandler {

    public static void handleBuyTicket(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO, PassengerDAO passengerDAO) {
        try {
            System.out.println("Получен запрос на покупку билета...");

            int userId = (int) sois.readObject();
            int flightId = Integer.parseInt((String) sois.readObject());
            String seat = (String) sois.readObject();
            String price = (String) sois.readObject();

            System.out.printf("Данные клиента: userId=%d, flightId=%d, seat=%s, price=%s%n", userId, flightId, seat, price);

            Integer passengerId = passengerDAO.getPassengerIdByUserId(userId);
            if (passengerId == null) {
                System.err.println("Пассажир с userId=" + userId + " не найден.");
                soos.writeObject("Passenger not found");
                return;
            }

            Passenger passenger = passengerDAO.getPassengerById(passengerId);
            if (passenger == null) {
                System.err.println("Ошибка получения данных пассажира с id=" + passengerId);
                soos.writeObject("Error: Passenger data not found");
                return;
            }

            BigDecimal ticketPrice = BigDecimal.valueOf(Double.parseDouble(price));

            if (passenger.getBalance().compareTo(ticketPrice) < 0) {
                System.err.println("Недостаточно средств на балансе для покупки билета.");
                soos.writeObject("Недостаточно средств");
                return;
            }

            passenger.setBalance(passenger.getBalance().subtract(ticketPrice));
            boolean isBalanceUpdated = passengerDAO.updatePassengerBalance(passenger);
            if (!isBalanceUpdated) {
                System.err.println("Не удалось обновить баланс пассажира.");
                soos.writeObject("Error: Failed to update balance");
                return;
            }

            Ticket ticket = new Ticket();
            ticket.setFlightID(flightId);
            ticket.setPassengerID(passengerId);
            ticket.setSeats(seat);
            ticket.setPrice(ticketPrice);
            ticket.setStatus("Closed");

            int seatNumber;
            try {
                seatNumber = Integer.parseInt(seat);
                if (seatNumber >= 1 && seatNumber <= 6) {
                    ticket.setTicketClass(TicketClass.BUSINESS);
                } else if (seatNumber >= 7 && seatNumber <= 18) {
                    ticket.setTicketClass(TicketClass.ECONOMY);
                } else {
                    System.err.println("Некорректный номер места: " + seat);
                    soos.writeObject("Error: Invalid seat number");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Не удалось преобразовать номер места в число: " + seat);
                soos.writeObject("Error: Seat number must be numeric");
                return;
            }

            boolean isCreated = ticketDAO.createTicket(ticket);
            if (!isCreated) {
                System.err.println("Не удалось создать билет.");
                soos.writeObject("Failed to create ticket");
                return;
            }

            Path receiptPath = ReceiptGenerator.generateTicketReceipt(ticket);

            // Отправка чека клиенту
            byte[] fileBytes = Files.readAllBytes(receiptPath);
            soos.writeObject("OK");
            soos.writeObject(fileBytes);
            System.out.println("Чек успешно создан и отправлен клиенту.");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void handleGenerateTicketReport(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO) {
        try {
            String fileName = (String) sois.readObject();
            List<Ticket> tickets = ticketDAO.getAllTickets();

            // Создаем путь для сохранения отчета относительно текущей директории
            String reportsDir = System.getProperty("user.dir") + File.separator + "Reports";
            Path reportPath = Paths.get(reportsDir, fileName);
            Files.createDirectories(reportPath.getParent()); // Убедиться, что директория существует

            // Создаем файл отчета
            try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {
                writer.write("=== Ticket Report ===\n");
                for (Ticket ticket : tickets) {
                    writer.write("Flight ID: " + ticket.getFlightID() + "\n");
                    writer.write("Passenger ID: " + ticket.getPassengerID() + "\n");
                    writer.write("Seat: " + ticket.getSeats() + "\n");
                    writer.write("Class: " + ticket.getTicketClass() + "\n");
                    writer.write("Price: " + ticket.getPrice() + "\n");
                    writer.write("Status: " + ticket.getStatus() + "\n");
                    writer.write("---------------------------\n");
                }
            }

            if (Files.exists(reportPath)) {
                // Читаем содержимое файла в байты
                byte[] fileBytes = Files.readAllBytes(reportPath);

                soos.writeObject("OK");
                soos.writeObject(fileBytes);
            } else {
                soos.writeObject("Error: Report file not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error while generating report: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void handleCancelTicket(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO, PassengerDAO passengerDAO) {
        try {
            String ticketIdString = (String) sois.readObject();
            Integer ticketId = Integer.parseInt(ticketIdString);

            Ticket ticket = ticketDAO.getTicketById(ticketId);
            if (ticket == null) {
                soos.writeObject("Error: Ticket not found.");
                return;
            }

            if ("Cancel".equalsIgnoreCase(ticket.getStatus())) {
                soos.writeObject("Error: Ticket is already cancelled.");
                return;
            }

            // Возвращаем деньги пользователю
            if (passengerDAO.addBalance(ticket.getPassengerID(), ticket.getPrice())) {
                if (ticketDAO.updateTicketStatus(ticketId, "Cancel")) {
                    soos.writeObject("OK");
                    System.out.println("Ticket cancelled and money refunded.");
                } else {
                    soos.writeObject("Error: Failed to update ticket status.");
                }
            } else {
                soos.writeObject("Error: Failed to refund money.");
            }
        } catch (Exception e) {
            System.err.println("Error cancelling ticket: " + e.getMessage());
            try {
                soos.writeObject("Error cancelling ticket: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGetAllTickets(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO) {
        try {
            List<Ticket> allTickets = ticketDAO.getAllTickets();
            if (allTickets != null && !allTickets.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(allTickets);
            } else {
                soos.writeObject("Error: No tickets found in the database.");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error retrieving tickets");
        }
    }

    public static void handleGetUserTickets(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO, PassengerDAO passengerDAO, FlightDAO flightDAO) {
        try {
            System.out.println("Получение билетов пользователя...");

            Object received = sois.readObject();
            int userId;

            if (received instanceof Integer) {
                userId = (Integer) received;
            } else if (received instanceof String) {
                try {
                    userId = Integer.parseInt((String) received);
                } catch (NumberFormatException e) {
                    soos.writeObject("Error: Invalid user ID format.");
                    return;
                }
            } else {
                soos.writeObject("Error: Unsupported data type for user ID.");
                return;
            }

            System.out.println("Получен userId: " + userId);

            int passengerId = passengerDAO.getPassengerIdByUserId(userId);
            System.out.println("Получен passengerId: " + passengerId);

            List<Ticket> tickets = ticketDAO.getTicketsByUserId(passengerId);
            if (tickets != null && !tickets.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(tickets);

                String confirmation = (String) sois.readObject();
                if ("OK".equals(confirmation)) {
                    List<Integer> flightIds = tickets.stream()
                            .map(Ticket::getFlightID)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    List<Flight> flights = flightDAO.getFlightsByTicketIds(flightIds);
                    soos.writeObject(flights);
                } else {
                    System.out.println("Клиент не подтвердил получение билетов.");
                }
            } else {
                soos.writeObject("No tickets found for user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error while fetching tickets or flights: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleCheckSeats(ObjectInputStream sois, ObjectOutputStream soos, TicketDAO ticketDAO) {
        try {
            Object received = sois.readObject();
            int flightID;

            if (received instanceof Integer) {
                flightID = (Integer) received;
            } else if (received instanceof String) {
                try {
                    flightID = Integer.parseInt((String) received);
                } catch (NumberFormatException e) {
                    soos.writeObject("Error: Invalid user ID format.");
                    return;
                }
            } else {
                soos.writeObject("Error: Unsupported data type for user ID.");
                return;
            }

            List<String> occupiedSeats = ticketDAO.getOccupiedSeats(flightID);

            soos.writeObject(occupiedSeats);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
}
