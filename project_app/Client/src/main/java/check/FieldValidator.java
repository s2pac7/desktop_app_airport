package check;

import javafx.scene.control.TextField;

public class FieldValidator {
    public static void configureTimeField(TextField timeField) {
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Удаляем все символы, кроме цифр и двоеточий
            String sanitized = newValue.replaceAll("[^\\d:]", "");

            // Ограничиваем длину до 5 символов
            if (sanitized.length() > 5) {
                timeField.setText(oldValue);
                return;
            }

            // Автоматически добавляем двоеточие после двух цифр
            if (sanitized.length() == 2 && !sanitized.contains(":")) {
                sanitized += ":";
            }

            // Проверяем корректность формата (HH:mm)
            if (sanitized.matches("\\d{0,2}:?\\d{0,2}")) {
                timeField.setText(sanitized);
            } else {
                timeField.setText(oldValue); // Если формат неверный, возвращаем предыдущее значение
            }

            // Устанавливаем курсор в конец текста
            timeField.positionCaret(sanitized.length());
        });
    }
}
