package iss.kienephongthuyfvix.uniportal.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectDao {
    private final Connection connection;

    public ObjectDao(Connection connection) {
        this.connection = connection;
    }

    // Fetch all grantees (users/roles) from the database
    public List<String> getGrantees(boolean isUser) throws SQLException {
        List<String> grantees = new ArrayList<>();
        String query = isUser
                ? "SELECT DISTINCT GRANTEE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE LIKE 'RL_%'"
                : "SELECT DISTINCT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE LIKE 'RL_%'";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            grantees.add(rs.getString(1));
        }
        return grantees;
    }

    // Fetch all object types (tables, views, procedures, etc.)
    public List<String> getObjectTypes() {
        List<String> objectTypes = new ArrayList<>();
        objectTypes.add("TABLE");
        objectTypes.add("VIEW");
        objectTypes.add("PROCEDURE");
        objectTypes.add("FUNCTION");
        return objectTypes;
    }

    // Fetch all object names of a specific type (tables, views, etc.)
    public List<String> getObjectNames(String objectType) throws SQLException {
        List<String> objectNames = new ArrayList<>();
        String query = "SELECT OBJECT_NAME FROM ALL_OBJECTS WHERE OWNER = 'QLDAIHOC' AND OBJECT_TYPE = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, objectType);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            objectNames.add(rs.getString("OBJECT_NAME"));
        }
        return objectNames;
    }

    // Fetch columns of a specific table for column-based permissions
    public List<String> getTableColumns(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        String query = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = ? AND OWNER = 'QLDAIHOC'";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, tableName.toUpperCase());
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            columns.add(rs.getString("COLUMN_NAME"));
        }
        return columns;
    }

    // Method to grant permission on an object
    public boolean grantPermission(String grantee, String objectType, String objectName, List<String> permissions, boolean withGrantOption) throws SQLException {
        String permissionClause = String.join(", ", permissions);
        String grantOption = withGrantOption ? " WITH GRANT OPTION" : "";

        String query = "GRANT " + permissionClause + " ON " + objectName + " TO " + grantee + grantOption;
        Statement stmt = connection.createStatement();
        int rowsAffected = stmt.executeUpdate(query);

        return rowsAffected > 0;
    }

    // Method to check if the grantee has the necessary permissions
    public boolean hasPermission(String grantee, String objectName, String permission) throws SQLException {
        String query = "SELECT COUNT(*) FROM DBA_TAB_PRIVS WHERE GRANTEE = ? AND TABLE_NAME = ? AND PRIVILEGE = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, grantee);
        ps.setString(2, objectName);
        ps.setString(3, permission);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}
