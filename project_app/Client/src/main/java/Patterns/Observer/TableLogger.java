package Patterns.Observer;

import javafx.collections.ObservableList;
import Pojo.Flight;

// Класс-наблюдатель, который будут выполнять конкретные действия при обновлении таблицы
public class TableLogger implements TableObserver {

    @Override
    public void update(ObservableList<Flight> flights) {
        System.out.println("Данные таблицы обновлены:");
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }
}
