package runServer.RequestHandler;

import Pojo.Aircraft;
import dao.AircraftDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class AircraftHandler {

    public static void handleAddAircraft(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            Aircraft newAircraft = (Aircraft) sois.readObject();
            if (aircraftDAO.addAircraft(newAircraft)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error adding aircraft");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error adding aircraft");
        }
    }

    public static void handleDeleteAircraft(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            int aircraftId = Integer.parseInt((String) sois.readObject());
            if (aircraftDAO.deleteAircraft(aircraftId)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error: Aircraft not found or could not be deleted.");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error deleting aircraft");
        }
    }

    public static void handleUpdateAircraft(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            Aircraft updatedAircraft = (Aircraft) sois.readObject();
            if (aircraftDAO.updateAircraft(updatedAircraft)) {
                soos.writeObject("OK");
            } else {
                soos.writeObject("Error updating aircraft");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error updating aircraft");
        }
    }

    public static void handleGetAllAircrafts(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            List<Aircraft> allAircrafts = aircraftDAO.getAllAircrafts();
            if (allAircrafts != null && !allAircrafts.isEmpty()) {
                soos.writeObject("OK");
                soos.writeObject(allAircrafts);
            } else {
                soos.writeObject("Error: No aircrafts found in the database.");
            }
        } catch (Exception e) {
            handleException(e, soos, "Error retrieving aircrafts");
        }
    }

    public static void handleSearchAircraftByName(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            String searchQuery = (String) sois.readObject();
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                soos.writeObject("Error: Search query cannot be empty.");
            } else {
                List<Aircraft> matchingAircrafts = aircraftDAO.searchAircraftsByName(searchQuery);
                if (matchingAircrafts != null && !matchingAircrafts.isEmpty()) {
                    soos.writeObject("OK");
                    soos.writeObject(matchingAircrafts);
                } else {
                    soos.writeObject("Error: No aircrafts matched the search query.");
                }
            }
        } catch (Exception e) {
            handleException(e, soos, "Error searching aircrafts");
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

    public static void handleGetAircraftNames(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            List<String> aircraftNames = aircraftDAO.getAllAircraftNames();
            soos.writeObject(aircraftNames);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void handleGetAircraftByName(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            String name = (String) sois.readObject();
            Aircraft aircraft = aircraftDAO.getAircraftByName(name);
            soos.writeObject(aircraft);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleGetNameAircraftById(ObjectInputStream sois, ObjectOutputStream soos, AircraftDAO aircraftDAO) {
        try {
            Object receivedObject = sois.readObject();
            if (receivedObject instanceof Integer) {
                int aircraftId = (Integer) receivedObject;
                String aircraftName = aircraftDAO.getAircraftNameById(aircraftId);
                soos.writeObject(aircraftName);
            } else {
                System.out.println("Received data is not of type Integer: " + receivedObject.getClass().getName());
                soos.writeObject("Error: Invalid data type");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                soos.writeObject("Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

