package Patterns.builders;

import Pojo.Flight;
import java.time.LocalDateTime;

public class FlightBuilder {
    private int aircraftID;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public FlightBuilder setAircraftID(int aircraftID) {
        this.aircraftID = aircraftID;
        return this;
    }

    public FlightBuilder setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
        return this;
    }

    public FlightBuilder setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        return this;
    }

    public FlightBuilder setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
        return this;
    }

    public FlightBuilder setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
        return this;
    }

    public FlightBuilder setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
        return this;
    }

    public Flight build() {
        Flight flight = new Flight();
        flight.setAircraftID(aircraftID);
        flight.setFlightNumber(flightNumber);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        return flight;
    }
}
