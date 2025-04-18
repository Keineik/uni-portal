package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDao {

    public ObservableList<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE LIKE 'RL_%'";

        System.out.println("Executing query: " + query);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String roleName = rs.getString("GRANTED_ROLE");
                roles.add(new Role(roleName));
            }
        }

        return FXCollections.observableArrayList(roles);
    }

    public void createRole(Role role) throws SQLException {
        String roleName = role.getRoleName();
        String createRoleSQL = "CREATE ROLE " + roleName;

        System.out.println("Executing SQL: " + createRoleSQL);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createRoleSQL);
        }
    }

    public void deleteRole(Role role) throws SQLException {
        String roleName = role.getRoleName();
        String dropRoleSQL = "DROP ROLE " + roleName;

        System.out.println("Executing SQL: " + dropRoleSQL);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dropRoleSQL);
        }
    }

    public void updateRole(Role role) {
    }
}
