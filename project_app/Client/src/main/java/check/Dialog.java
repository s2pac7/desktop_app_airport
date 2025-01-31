package check;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.time.LocalDate;
import java.time.Period;

public class Dialog {

    public static void showAlertWithNullInput() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Поля не могут быть пустыми!");
        alert.setContentText("Пожалуйста, введите логин и пароль.");
        alert.showAndWait();
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showAlertWithNoLogin() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка авторизации");
        alert.setHeaderText("Неверный логин или пароль");
        alert.setContentText("Пожалуйста, проверьте данные и попробуйте снова.");
        alert.showAndWait();
    }

    public static void showAlertWithExistLogin() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка регистрации");
        alert.setHeaderText("Пользователь с таким логином уже существует");
        alert.setContentText("Пожалуйста, выберите другой логин.");
        alert.showAndWait();
    }

    public static void showAlertWithInvalidCredentials() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка авторизации");
        alert.setHeaderText("Неверные учетные данные");
        alert.setContentText("Пожалуйста, проверьте логин и пароль и попробуйте снова.");
        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("\\+\\d{10,15}");
    }

    public static boolean isPassportNumberValid(String passportNumber) {
        return passportNumber.matches("[A-Z]{2}\\d{7}");
    }

    public static boolean isAgeValid(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false;
        LocalDate today = LocalDate.now();
        Period age = Period.between(dateOfBirth, today);
        return age.getYears() >= 18;
    }

    // Форматирование номера карты
    public static String formatCardNumber(String input) {
        input = input.replaceAll("\\s", "").replaceAll("[^\\d]", ""); // Удаление всех пробелов и нечисловых символов
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(input.charAt(i));
        }
        return formatted.toString();
    }

    // Форматирование срока действия карты
    public static String formatCardValidity(String input) {
        input = input.replaceAll("[^\\d]", ""); // Удаление всех нечисловых символов
        if (input.length() > 2) {
            input = input.substring(0, 2) + "/" + input.substring(2);
        }
        if (input.length() > 5) {
            input = input.substring(0, 5);
        }
        return input;
    }
}
