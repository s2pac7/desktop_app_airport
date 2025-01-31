package Pojo;

import EnumPackage.TicketClass;

import java.io.Serializable;
import java.math.BigDecimal;

public class Ticket implements Serializable {
    private Integer id;
    private Integer flightID;
    private Integer passengerID;
    private String seats;
    private TicketClass ticketClass;
    private BigDecimal price;
    private String status;

    public Ticket() {
    }

    public Ticket(Integer id, Integer flightID, Integer passengerID, String seats, TicketClass ticketClass, BigDecimal price, String status) {
        this.id = id;
        this.flightID = flightID;
        this.passengerID = passengerID;
        this.seats = seats;
        this.ticketClass = ticketClass;
        this.price = price;
        this.status = status;
    }

    public Ticket(Integer id, Integer flightID, String seats, TicketClass ticketClass, BigDecimal price, String status) {
        this.id = id;
        this.flightID = flightID;
        this.seats = seats;
        this.ticketClass = ticketClass;
        this.price = price;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFlightID() {
        return flightID;
    }

    public void setFlightID(Integer flightID) {
        this.flightID = flightID;
    }

    public Integer getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(Integer passengerID) {
        this.passengerID = passengerID;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public TicketClass getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(TicketClass ticketClass) {
        this.ticketClass = ticketClass;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", flightID=" + flightID +
                ", passengerID=" + passengerID +
                ", seats='" + seats + '\'' +
                ", ticketClass='" + ticketClass + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
