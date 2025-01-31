package Session;

public class SessionData {
    private static int currentUserId = -1; // Хранение ID текущего пользователя

    // Метод для получения ID пользователя
    public static int getCurrentUserId() {
        return currentUserId;
    }

    // Метод для установки ID пользователя
    public static void setCurrentUserIdSession(int userId) {
        currentUserId = userId;
    }

    // Метод для сброса данных
    public static void clearSession() {
        currentUserId = -1;
    }
}
