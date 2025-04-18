package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Role {
    private final StringProperty roleName;

    public Role(String roleName) {
        this.roleName = new SimpleStringProperty(roleName);
    }


    public String getRoleName() {
        return roleName.get();
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName.get();
    }
}
