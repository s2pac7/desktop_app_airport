package Patterns.Observer;

import Pojo.User;
import javafx.collections.ObservableList;

public class UserTableObserver implements UserObserver {
    private final User user;

    public UserTableObserver(User user) {
        this.user = user;
    }

    @Override
    public void update(User user) {
        System.out.println("User " + user.getUsername() + " has received an update.");
    }
}
