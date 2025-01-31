package controllers;

import Session.SessionData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import runClient.Connect;

import java.io.IOException;
import java.math.BigDecimal;

public class AddMoneyController {

    @FXML
    private Button addMoneyButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField cardМalidity;

    @FXML
    private TextField numbersBank;

    @FXML
    private TextField sumMoney;

    @FXML
    void initialize() {
        backButton.setOnAction(event -> backToUserMenu());
        addMoneyButton.setOnAction(event -> processAddMoney());

        // Добавление форматирования для номера карты
        numbersBank.textProperty().addListener((observable, oldValue, newValue) -> {
            String formatted = formatCardNumber(newValue);
            if (!formatted.equals(newValue)) {
                numbersBank.setText(formatted);
                numbersBank.positionCaret(formatted.length());
            }
        });

        // Ограничение на ввод не более 16 цифр
        numbersBank.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitsOnly = newValue.replaceAll("\\D", ""); // Убираем все, кроме цифр
            if (digitsOnly.length() > 16) {
                numbersBank.setText(oldValue); // Возвращаем предыдущее значение, если длина больше 16
            } else {
                // Форматируем номер карты с пробелами только до 16 цифры
                String formatted = formatCardNumber(digitsOnly);
                if (!formatted.equals(newValue)) {
                    numbersBank.setText(formatted);
                    numbersBank.positionCaret(formatted.length()); // Устанавливаем курсор в конец
                }
            }
        });

        // Добавление форматирования для срока действия карты
        cardМalidity.textProperty().addListener((observable, oldValue, newValue) -> {
            cardМalidity.setText(formatCardValidity(newValue));
            cardМalidity.positionCaret(cardМalidity.getText().length());
        });
    }

    private String formatCardNumber(String input) {
        String digits = input.replaceAll("\\D", ""); // Убираем все, кроме цифр
        StringBuilder formatted = new StringBuilder();

        // Добавляем пробелы после каждых 4 цифр, но только если длина номера карты меньше или равна 16
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0 && i < 16) {
                formatted.append("    "); // Добавляем пробел между группами цифр
            }
            formatted.append(digits.charAt(i));
        }

        return formatted.toString();
    }

    private String formatCardValidity(String input) {
        String digits = input.replaceAll("\\D", "");
        if (digits.length() > 4) {
            digits = digits.substring(0, 4);
        }
        if (digits.length() <= 2) {
            return digits;
        } else {
            return digits.substring(0, 2) + "/" + digits.substring(2);
        }
    }

    private void backToUserMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageFlightsUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAddMoney() {
        int currentUserId = SessionData.getCurrentUserId();

        if (currentUserId == -1) {
            showAlert("Ошибка", "Идентификатор пользователя не установлен. Пожалуйста, повторите вход.");
            return;
        }

        String cardNumber = numbersBank.getText().replace(" ", "").trim();
        String cardValidity = cardМalidity.getText().trim();
        String sumText = sumMoney.getText().trim();

        if (cardNumber.isEmpty() || cardValidity.isEmpty() || sumText.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }

        if (!cardNumber.matches("\\d{16}")) {
            showAlert("Ошибка", "Номер карты должен содержать 16 цифр.");
            return;
        }

        if (!isValidCardNumber(cardNumber)) {
            showAlert("Ошибка", "Введённый номер карты недействителен.");
            return;
        }

        if (!cardValidity.matches("\\d{2}/\\d{2}")) {
            showAlert("Ошибка", "Срок действия карты должен быть в формате MM/YY.");
            return;
        }

        // Проверка актуальности срока действия
        if (!isCardValidityCurrent(cardValidity)) {
            showAlert("Ошибка", "Срок действия карты истек.");
            return;
        }

        BigDecimal sum;
        try {
            sum = new BigDecimal(sumText);
            if (sum.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Ошибка", "Сумма должна быть положительной.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректное числовое значение для суммы.");
            return;
        }

        try {
            Connect.client.sendMessage("addBalance");
            Connect.client.sendMessage(String.valueOf(currentUserId));
            Connect.client.sendMessage(sum.toString());

            String response = (String) Connect.client.readObject();
            if ("OK".equals(response)) {
                Platform.runLater(() -> {
                    showAlert("Успех", "Баланс успешно пополнен.");
                    closeWindow();
                });
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось пополнить баланс. Попробуйте позже.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) addMoneyButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isCardValidityCurrent(String cardValidity) {
        String[] parts = cardValidity.split("/");
        if (parts.length != 2) return false;

        int month;
        int year;
        try {
            month = Integer.parseInt(parts[0]);
            year = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        if (month < 1 || month > 12) return false;

        // Текущая дата
        java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
        int currentYear = currentYearMonth.getYear() % 100; // Берем последние две цифры года
        int currentMonth = currentYearMonth.getMonthValue();

        // Проверка
        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return false; // Срок действия истек
        }

        return true; // Карта действительна
    }


    private boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Проходим с конца строки
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                n *= 2; // Удваиваем каждую вторую цифру
                if (n > 9) {
                    n -= 9; // Если результат больше 9, вычитаем 9
                }
            }
            sum += n;
            alternate = !alternate; // Меняем флаг
        }

        // Проверяем, делится ли сумма на 10
        return (sum % 10 == 0);
    }
}





// 4539 1488 0343 6467
// 4716 9413 5097 9358
// 4916 7610 1361 5172
// 5500 0000 0000 0004
// 5200 8282 8282 8210
// 5555 5555 5555 4444