package ReceiptGenerator;

import Pojo.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class ReceiptGenerator {

    public static Path generateTicketReceipt(Ticket ticket) throws IOException {
        // Указываем директорию для сохранения чеков
        String receiptsDir = System.getProperty("user.dir") + File.separator + "Receipts";
        // Создаем путь к файлу
        Path receiptPath = Paths.get(receiptsDir, "ticket_receipt_" + ticket.getId() + ".txt");
        // Создаем директории, если они отсутствуют
        Files.createDirectories(receiptPath.getParent());

        // Пишем данные в файл
        try (BufferedWriter writer = Files.newBufferedWriter(receiptPath)) {
            writer.write("=== Ticket Receipt ===\n");
            writer.write("Flight ID: " + ticket.getFlightID() + "\n");
            writer.write("Passenger ID: " + ticket.getPassengerID() + "\n");
            writer.write("Seat: " + ticket.getSeats() + "\n");
            writer.write("Class: " + ticket.getTicketClass() + "\n");
            writer.write("Price: " + ticket.getPrice() + "\n");
            writer.write("Status: " + ticket.getStatus() + "\n");
            writer.write("Date: " + java.time.LocalDateTime.now() + "\n");
        }

        return receiptPath;
    }
}