package Patterns.builders;

import Pojo.Passenger;
import java.math.BigDecimal;

public class PassengerBuilder {
    private int userId;
    private String name;
    private String surname;
    private String passportNumber;
    private String dateOfBirth;
    private String phoneNumber;
    private BigDecimal balance;

    public PassengerBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public PassengerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PassengerBuilder setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public PassengerBuilder setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
        return this;
    }

    public PassengerBuilder setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public PassengerBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PassengerBuilder setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public Passenger build() {
        return new Passenger(userId, name, surname, passportNumber, dateOfBirth, phoneNumber, balance);
    }
}
