package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {
    private final StringProperty userId;
    private final StringProperty username;
    private final ObservableList<String> roles = FXCollections.observableArrayList();

    public User(String userId, String username, String... initialRoles) {
        this.userId = new SimpleStringProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.roles.addAll(initialRoles);
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getUserId() {
        return userId.get();
    }

    public String getUsername() {
        return username.get();
    }

    public void setUserId(String id) {
        userId.set(id);
    }

    public void setUsername(String name) {
        username.set(name);
    }

    public ObservableList<String> getRoles() {
        return roles;
    }


}