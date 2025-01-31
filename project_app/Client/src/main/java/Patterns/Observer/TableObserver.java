package Patterns.Observer;

import javafx.collections.ObservableList;
import Pojo.Flight;

public interface TableObserver {
    void update(ObservableList<Flight> flights);
}