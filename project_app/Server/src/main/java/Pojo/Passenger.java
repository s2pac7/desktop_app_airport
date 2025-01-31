package Pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Passenger implements Serializable {

    private int id;
    private int userId;
    private String name;
    private String surname;
    private String passportNumber;
    private String dateOfBirth;
    private String phoneNumber;
    String username;
    private BigDecimal balance = BigDecimal.ZERO;


    public Passenger() {}

    public Passenger(int userId, String name, String surname, String passportNumber, String dateOfBirth, String phoneNumber, BigDecimal balance) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
    }
    public Passenger(int userId, String name, String surname, String passportNumber, String dateOfBirth, String phoneNumber, BigDecimal balance, String username) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return id == passenger.id &&
                userId == passenger.userId &&
                Objects.equals(name, passenger.name) &&
                Objects.equals(surname, passenger.surname) &&
                Objects.equals(passportNumber, passenger.passportNumber) &&
                Objects.equals(dateOfBirth, passenger.dateOfBirth) &&
                Objects.equals(phoneNumber, passenger.phoneNumber) &&
                Objects.equals(balance, passenger.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, surname, passportNumber, dateOfBirth, phoneNumber, balance);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
}
