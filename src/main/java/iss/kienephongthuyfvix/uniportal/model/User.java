package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {
    private final StringProperty username;
    private final ObservableList<String> roles = FXCollections.observableArrayList();

    public User(String username, String... initialRoles) {
        this.username = new SimpleStringProperty(username);
        this.roles.addAll(FXCollections.observableArrayList(initialRoles));
    }

    public StringProperty usernameProperty() {
        return username;
    }
    public String getUsername() {
        return username.get();
    }

    public void setUsername(String name) {
        username.set(name);
    }

    public ObservableList<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(String role) {
        roles.remove(role);
    }

}
