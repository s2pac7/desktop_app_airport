package Pojo;

import java.io.Serializable;

public class Aircraft implements Serializable  {
    private int id;
    private String aircraftName;
    private String aircraftType;

    public Aircraft(int id, String aircraftName, String aircraftType) {
        this.id = id;
        this.aircraftName = aircraftName;
        this.aircraftType = aircraftType;
    }

    public Aircraft(String aircraftName, String aircraftType) {
        this.aircraftName = aircraftName;
        this.aircraftType = aircraftType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }
}
