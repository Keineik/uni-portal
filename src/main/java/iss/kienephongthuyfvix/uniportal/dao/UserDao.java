package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.Privilege;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import iss.kienephongthuyfvix.uniportal.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    public List<User> getAllUsers() throws SQLException {
        Map<String, User> userMap = new HashMap<>();

        String query =
                "SELECT DISTINCT rp.GRANTEE, rp.GRANTED_ROLE " +
                        "FROM DBA_ROLE_PRIVS rp " +
                        "JOIN DBA_TAB_PRIVS tp ON rp.GRANTED_ROLE = tp.GRANTEE " +
                        "JOIN DBA_USERS u ON rp.GRANTEE = u.USERNAME " +
                        "WHERE rp.GRANTEE != 'SYSTEM' AND tp.OWNER = 'QLDAIHOC' " +
                        "AND rp.GRANTED_ROLE NOT IN ('CONNECT', 'RESOURCE') " +
                        "ORDER BY rp.GRANTEE";



        System.out.println("Executing query: " + query);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("GRANTEE");
                String role = rs.getString("GRANTED_ROLE");

                User user = userMap.get(username);
                if (user == null) {
                    user = new User(username);
                    userMap.put(username, user);
                }
                user.addRole(role);
            }
        }

        return new ArrayList<>(userMap.values());
    }

    public void createUser(User user) throws SQLException {
        String createUserSQL = "CREATE USER " + user.getUsername() + " IDENTIFIED BY " + user.getUsername();
        String grantRoleSQL = "GRANT " + String.join(", ", user.getRoles()) + " TO " + user.getUsername();

        log.info("Executing SQL: {}", createUserSQL);
        log.info("Executing SQL: {}", grantRoleSQL);


        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createUserSQL);
            stmt.executeUpdate(grantRoleSQL);
        }
    }

    public void updateUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            List<String> currentRoles = getRolesOfUser(user.getUsername(), conn);
            for (String role : currentRoles) {
                String revokeSQL = "REVOKE " + role + " FROM " + user.getUsername();
                System.out.println("Executing SQL: " + revokeSQL);
                stmt.executeUpdate(revokeSQL);
            }

            String grantRoleSQL = "GRANT " + String.join(", ", user.getRoles()) + " TO " + user.getUsername();
            System.out.println("Executing SQL: " + grantRoleSQL);
            stmt.executeUpdate(grantRoleSQL);
        }
    }

    public void deleteUser(User user) throws SQLException {
        String alterSession = "ALTER SESSION SET \"_oracle_script\"=TRUE";
        String dropUserSQL = "DROP USER " + user.getUsername() + " CASCADE";
        System.out.println("Executing SQL: " + dropUserSQL);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(alterSession);
            stmt.executeUpdate(dropUserSQL);
        }
    }

    public List<String> getRolesOfUser(String username, Connection conn) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTEE = ?";

        System.out.println("Executing SQL: " + sql + " with parameter: " + username);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("GRANTED_ROLE"));
                }
            }
        }
        return roles;
    }

    public List<String> getCurrentUserRoles() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            List<String> roles = new ArrayList<>();
            String sql = "SELECT ROLE FROM SESSION_ROLES";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("ROLE"));
                }
            }
            return roles;
        }
    }

    public List<Privilege> getPrivilegesByUser(String userName) throws SQLException {
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
                ps1.setString(1, userName);
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
                ps2.setString(1, userName);
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
