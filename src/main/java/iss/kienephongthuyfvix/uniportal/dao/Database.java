package iss.kienephongthuyfvix.uniportal.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class Database {
    private static HikariDataSource dataSource;

    public static void initialize(String username, String password) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/XE");
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            config.setSchema("QLDAIHOC");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);

            if (dataSource != null) {
                dataSource.close();
            }
            dataSource = new HikariDataSource(config);
            log.info("HikariCP initialized successfully with user: {}", username);
        } catch (Exception e) {
            log.error("Error initializing HikariCP: {}", e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("HikariCP is not initialized. Please log in first.");
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            log.info("HikariCP data source closed.");
        }
    }
}