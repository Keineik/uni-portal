package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.Privilege;
import iss.kienephongthuyfvix.uniportal.model.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.*;

public class RoleDao {

    public ObservableList<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT DISTINCT rp.GRANTED_ROLE " +
                "FROM DBA_ROLE_PRIVS rp WHERE GRANTED_ROLE LIKE 'RL%' AND GRANTED_ROLE NOT IN ('CONNECT', 'RESOURCE') AND GRANTED_ROLE != 'RL_ADMIN'" +
                "ORDER BY rp.GRANTED_ROLE";

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

    public List<Privilege> getPrivilegesByRole(String roleName) throws SQLException {
        List<Privilege> privileges = new ArrayList<>();

        String tablePrivsQuery = "SELECT TABLE_NAME, PRIVILEGE, GRANTABLE " +
                "FROM DBA_TAB_PRIVS " +
                "WHERE GRANTEE = ? AND OWNER = 'QLDAIHOC'";

        String columnPrivsQuery = "SELECT TABLE_NAME, COLUMN_NAME, PRIVILEGE, GRANTABLE " +
                "FROM DBA_COL_PRIVS " +
                "WHERE GRANTEE = ? AND OWNER = 'QLDAIHOC'";

        try (Connection conn = Database.getConnection()) {

            // TABLE-LEVEL PRIVILEGES
            try (PreparedStatement ps1 = conn.prepareStatement(tablePrivsQuery)) {
                ps1.setString(1, roleName);
                ResultSet rs1 = ps1.executeQuery();

                Map<String, List<String>> privMap = new HashMap<>();
                Map<String, List<String>> withGrantMap = new HashMap<>();

                while (rs1.next()) {
                    String object = rs1.getString("TABLE_NAME");
                    String priv = rs1.getString("PRIVILEGE");
                    boolean grantable = rs1.getString("GRANTABLE").equalsIgnoreCase("YES");

                    privMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    if (grantable) {
                        withGrantMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    }
                }

                for (String object : privMap.keySet()) {
                    privileges.add(new Privilege(
                            object,
                            "TABLE",
                            privMap.get(object),
                            withGrantMap.getOrDefault(object, List.of()),
                            List.of()
                    ));
                }
            }

            // COLUMN-LEVEL PRIVILEGES
            try (PreparedStatement ps2 = conn.prepareStatement(columnPrivsQuery)) {
                ps2.setString(1, roleName);
                ResultSet rs2 = ps2.executeQuery();

                // Map<object, Map<privilege, List<columns>>>
                Map<String, List<String>> updateColumnsMap = new HashMap<>();
                Map<String, List<String>> selectColumnsMap = new HashMap<>();
                Map<String, List<String>> withGrantMap = new HashMap<>();
                Map<String, Set<String>> privilegesMap = new HashMap<>();

                while (rs2.next()) {
                    String object = rs2.getString("TABLE_NAME");
                    String column = rs2.getString("COLUMN_NAME");
                    String priv = rs2.getString("PRIVILEGE");
                    boolean grantable = rs2.getString("GRANTABLE").equalsIgnoreCase("YES");

                    privilegesMap.computeIfAbsent(object, k -> new HashSet<>()).add(priv);

                    if (priv.equalsIgnoreCase("UPDATE")) {
                        updateColumnsMap.computeIfAbsent(object, k -> new ArrayList<>()).add(column);
                    } else {
                        selectColumnsMap.computeIfAbsent(object, k -> new ArrayList<>()).add(column);
                    }

                    if (grantable) {
                        withGrantMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    }
                }

                for (String object : privilegesMap.keySet()) {
                    privileges.add(new Privilege(
                            object,
                            "COLUMN",
                            new ArrayList<>(privilegesMap.get(object)),
                            withGrantMap.getOrDefault(object, List.of()),
                            updateColumnsMap.getOrDefault(object, List.of())
                    ));
                }
            }
        }

        return privileges;
    }
}
