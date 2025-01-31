package runServer.RequestHandler;

import Pojo.Flight;
import dao.FlightDAO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class FlightHandler {
    public static void handleGetAllFlights(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            List<Flight> allFlights = flightDAO.getAllFlights();
            if (allFlights != null && !allFlights.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(allFlights);
            } else {
                soos.writeObject("Error: No flights found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error retrieving flights: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGetOnlineTableData(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            List<Flight> allFlights = flightDAO.getAllFlights();
            if (allFlights != null && !allFlights.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(allFlights);
            } else {
                soos.writeObject("Error: No flights found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error retrieving flights: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGetAllFlightsForBuy(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            List<Flight> allFlights = flightDAO.getAllFlights();
            if (allFlights != null && !allFlights.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(allFlights);
            } else {
                soos.writeObject("Error: No flights found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error retrieving flights: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGenerateReport(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            String fileName = (String) sois.readObject(); // Имя файла
            List<Flight> flights = flightDAO.getAllFlights(); // Получаем данные о рейсах

            // Генерация отчёта в формате текстового документа
            String reportsDir = System.getProperty("user.dir") + File.separator + "Reports";
            File directory = new File(reportsDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Создать папку Reports, если её нет
            }

            Path reportPath = Paths.get(reportsDir, fileName);
            try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {
                writer.write("Отчет о рейсах:\n");
                writer.write("====================\n\n");
                for (Flight flight : flights) {
                    writer.write("ID рейса: " + flight.getId() + "\n");
                    writer.write("Номер рейса: " + flight.getFlightNumber() + "\n");
                    writer.write("Аэропорт вылета: " + flight.getDepartureAirport() + "\n");
                    writer.write("Аэропорт прилета: " + flight.getArrivalAirport() + "\n");
                    writer.write("Время вылета: " + flight.getDepartureTime() + "\n");
                    writer.write("Время прилета: " + flight.getArrivalTime() + "\n");
                    writer.write("Название самолета: " + flight.getNameAircraft() + "\n");
                    writer.write("--------------------\n");
                }
            }

            File reportFile = reportPath.toFile();
            if (reportFile.exists()) {
                byte[] fileBytes = Files.readAllBytes(reportFile.toPath());
                soos.writeObject("OK");
                soos.writeObject(fileBytes); // Отправляем файл клиенту
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


    public static void handleAddFlight(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            Flight newFlight = (Flight) sois.readObject();
            boolean success = flightDAO.addFlight(newFlight);
            if (success) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error: Не удалось добавить рейс.");
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleSearchFlightByNumber(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            String searchQuery = (String) sois.readObject();
            List<Flight> foundFlights = flightDAO.searchFlightByNumber(searchQuery);
            soos.writeObject("OK");
            soos.writeObject(foundFlights);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleUpdateFlight(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            Flight updatedFlight = (Flight) sois.readObject();
            if (flightDAO.updateFlight(updatedFlight)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error updating flight");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error updating flight: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleDeleteFlight(ObjectInputStream sois, ObjectOutputStream soos, FlightDAO flightDAO) {
        try {
            int flightId = Integer.parseInt((String) sois.readObject());
            boolean isDeleted = flightDAO.deleteFlightById(flightId);
            soos.writeObject(isDeleted ? "OK" : "Не удалось удалить рейс.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
