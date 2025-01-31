package dao;

import Pojo.Aircraft;

import java.sql.SQLException;
import java.util.List;

public interface IAircraftDAO {
    boolean addAircraft(Aircraft aircraft) throws SQLException;

    boolean deleteAircraft(int id);

    List<Aircraft> getAllAircrafts();

    boolean updateAircraft(Aircraft aircraft) throws SQLException;

    List<Aircraft> searchAircraftsByName(String searchQuery) throws SQLException;

    List<String> getAllAircraftNames();

    Aircraft getAircraftByName(String name);

    String getAircraftNameById(int id);
}