package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import iss.kienephongthuyfvix.uniportal.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {
    public List<User> getAllUsers() throws SQLException {
        Map<String, User> userMap = new HashMap<>();

        String query = "SELECT GRANTEE, GRANTED_ROLE " +
                "FROM DBA_ROLE_PRIVS " +
                "WHERE GRANTED_ROLE NOT IN ('CONNECT', 'RESOURCE') " +
                "AND GRANTEE IN (" +
                "    SELECT MANV FROM QLDAIHOC.NHANVIEN " +
                "    UNION " +
                "    SELECT MASV FROM QLDAIHOC.SINHVIEN" +
                ") " +
                "ORDER BY GRANTEE";

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

        System.out.println("Executing SQL: " + createUserSQL);
        System.out.println("Executing SQL: " + grantRoleSQL);

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
        String dropUserSQL = "DROP USER " + user.getUsername() + " CASCADE";
        System.out.println("Executing SQL: " + dropUserSQL);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(dropUserSQL);
        }
    }

    private List<String> getRolesOfUser(String username, Connection conn) throws SQLException {
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
}
