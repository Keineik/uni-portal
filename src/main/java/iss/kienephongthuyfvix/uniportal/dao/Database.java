package iss.kienephongthuyfvix.uniportal.dao;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class Database {
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
    private static Connection conn;

    public static Connection getConnection(String username, String password) {
        try {
            // Load the Oracle JDBC driver
            Class.forName(DRIVER_NAME);
            // Close any existing connection before creating a new one
            if (conn != null && !conn.isClosed()) conn.close();
            // Establish connection with provided user credentials
            conn = DriverManager.getConnection(URL, username, password);
            log.info("Connection established");
        } catch (ClassNotFoundException e) {
            log.error("Oracle JDBC Driver not found: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("Error while connecting to Oracle JDBC Driver: {}", e.getMessage());
            conn = null; // Ensure conn is null if connection fails
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                log.info("Connection closed");
            }
        } catch (SQLException e) {
            log.error("Error while closing connection: {}", e.getMessage());
        }
    }

    public static Connection getCurrentConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("No active database connection. Please log in.");
        }
        return conn;
    }
}