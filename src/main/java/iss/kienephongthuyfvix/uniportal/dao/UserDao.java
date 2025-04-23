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
import java.util.stream.Collectors;

public class UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    public List<User> getAllUsers() throws SQLException {
        Map<String, User> userMap = new HashMap<>();

        String query =
                "SELECT DISTINCT rp.GRANTEE, rp.GRANTED_ROLE " +
                        "FROM DBA_ROLE_PRIVS rp " +
                        "JOIN DBA_TAB_PRIVS tp ON rp.GRANTED_ROLE = tp.GRANTEE " +
                        "JOIN DBA_USERS u ON rp.GRANTEE = u.USERNAME " +
                        "WHERE rp.GRANTEE NOT IN ('SYS','SYSTEM', 'QLDAIHOC') AND tp.OWNER = 'QLDAIHOC' " +
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
//            stmt.executeUpdate(alterSession);
            stmt.executeUpdate(dropUserSQL);
        }
    }

    public void changePassword(String username, String newPassword) throws SQLException {
        String sql = "ALTER USER " + username + " IDENTIFIED BY \"" + newPassword + "\"";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Executing SQL: " + sql);
            stmt.executeUpdate(sql);
        }
    }


    public List<String> getRolesOfUser(String username, Connection conn) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTEE = ? AND GRANTED_ROLE NOT IN ('CONNECT', 'RESOURCE')";

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

        try (Connection conn = Database.getConnection()) {
            List<String> userRoles = getRolesOfUser(userName, conn);
            userRoles.add(userName);

            String inClause = userRoles.stream()
                    .map(role -> "'" + role + "'")
                    .collect(Collectors.joining(","));

            String tablePrivsQuery = "SELECT TABLE_NAME, PRIVILEGE, GRANTABLE, GRANTEE " +
                    "FROM DBA_TAB_PRIVS " +
                    "WHERE GRANTEE IN (" + inClause + ") AND OWNER = 'QLDAIHOC'";

            String columnPrivsQuery = "SELECT TABLE_NAME, COLUMN_NAME, PRIVILEGE, GRANTABLE, GRANTEE " +
                    "FROM DBA_COL_PRIVS " +
                    "WHERE GRANTEE IN (" + inClause + ") AND OWNER = 'QLDAIHOC'";

            // --- TABLE-LEVEL PRIVILEGES ---
            System.out.println("Executing SQL (TABLE): " + tablePrivsQuery);
            try (Statement stmt1 = conn.createStatement();
                 ResultSet rs1 = stmt1.executeQuery(tablePrivsQuery)) {

                Map<String, List<String>> privMap = new HashMap<>();
                Map<String, List<String>> withGrantMap = new HashMap<>();
                Map<String, Set<String>> granteeMap = new HashMap<>();

                while (rs1.next()) {
                    String object = rs1.getString("TABLE_NAME");
                    String priv = rs1.getString("PRIVILEGE");
                    boolean grantable = rs1.getString("GRANTABLE").equalsIgnoreCase("YES");
                    String grantee = rs1.getString("GRANTEE");

                    privMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    granteeMap.computeIfAbsent(object + "_" + priv, k -> new HashSet<>()).add(grantee);

                    if (grantable) {
                        withGrantMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    }
                }

                for (String object : privMap.keySet()) {
                    List<String> privList = privMap.get(object);
                    Set<String> grantees = new HashSet<>();
                    for (String priv : privList) {
                        grantees.addAll(granteeMap.get(object + "_" + priv));
                    }

                    Privilege p = new Privilege(
                            object,
                            "TABLE",
                            privList,
                            withGrantMap.getOrDefault(object, List.of()),
                            List.of()
                    );

                    // Join the grantees list as a comma-separated string
                    String granteeDisplay = String.join(", ", grantees);
                    p.setGrantee(granteeDisplay);

                    privileges.add(p);
                }
            }

            // --- COLUMN-LEVEL PRIVILEGES ---
            System.out.println("Executing SQL (COLUMN): " + columnPrivsQuery);
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery(columnPrivsQuery)) {

                Map<String, Set<String>> privilegesMap = new HashMap<>();
                Map<String, List<String>> updateColumnsMap = new HashMap<>();
                Map<String, List<String>> withGrantMap = new HashMap<>();
                Map<String, Set<String>> granteeMap = new HashMap<>();

                while (rs2.next()) {
                    String object = rs2.getString("TABLE_NAME");
                    String column = rs2.getString("COLUMN_NAME");
                    String priv = rs2.getString("PRIVILEGE");
                    boolean grantable = rs2.getString("GRANTABLE").equalsIgnoreCase("YES");
                    String grantee = rs2.getString("GRANTEE");

                    privilegesMap.computeIfAbsent(object, k -> new HashSet<>()).add(priv);

                    if (priv.equalsIgnoreCase("UPDATE")) {
                        updateColumnsMap.computeIfAbsent(object, k -> new ArrayList<>()).add(column);
                    }

                    if (grantable) {
                        withGrantMap.computeIfAbsent(object, k -> new ArrayList<>()).add(priv);
                    }

                    granteeMap.computeIfAbsent(object + "_" + priv, k -> new HashSet<>()).add(grantee);
                }

                for (String object : privilegesMap.keySet()) {
                    List<String> privList = new ArrayList<>(privilegesMap.get(object));
                    Set<String> grantees = new HashSet<>();
                    for (String priv : privList) {
                        grantees.addAll(granteeMap.get(object + "_" + priv));
                    }

                    Privilege p = new Privilege(
                            object,
                            "COLUMN",
                            privList,
                            withGrantMap.getOrDefault(object, List.of()),
                            updateColumnsMap.getOrDefault(object, List.of())
                    );

                    // Join the grantees list as a comma-separated string
                    String granteeDisplay = String.join(", ", grantees);
                    p.setGrantee(granteeDisplay);

                    privileges.add(p);
                }
            }
        }

        return privileges;
    }



    // Revoke privilege from user
    public void revokePrivilege(String username, Privilege privilege) throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql;
            boolean isPrivilegeFromRole = false;

            if (!isPrivilegeFromRole) {
                for (String priv : privilege.getPrivileges()) {
                    sql = String.format("REVOKE %s ON %s FROM %s", priv, privilege.getObject(), username);
                    System.out.println("Executing SQL: " + sql);
                    stmt.executeUpdate(sql);
                }
            } else {
                System.out.println("Cannot REVOKE privilege granted via role.");
            }
        }
    }


}
