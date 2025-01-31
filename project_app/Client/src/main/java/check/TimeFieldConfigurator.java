package check;

import javafx.scene.control.TextField;

public class TimeFieldConfigurator {

    public static void configureTimeField(TextField timeField) {
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Удаляем все, кроме цифр и двоеточия
            String sanitized = newValue.replaceAll("[^\\d:]", "");

            // Если пользователь пытается ввести больше 5 символов
            if (sanitized.length() > 5) {
                timeField.setText(oldValue);
                return;
            }

            // Автоматически добавляем ':' после двух цифр
            if (sanitized.length() == 2 && !sanitized.contains(":")) {
                sanitized += ":";
            }

            // Если формат корректный, применяем
            if (sanitized.matches("\\d{0,2}:?\\d{0,2}")) {
                timeField.setText(sanitized);
            } else {
                timeField.setText(oldValue); // Восстанавливаем старое значение при ошибке
            }

            // Устанавливаем курсор в конец текста
            timeField.positionCaret(sanitized.length());
        });
    }
}
