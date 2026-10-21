package com.movieticket.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Driver;

public class DBConnection {
    // Oracle Database 19c connection details
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USERNAME = "system";
    private static final String PASSWORD = "livewire";

    static {
        try {
            // Explicitly instantiate and register Oracle driver to ensure registration across all classloaders
            Class<?> driverClass = Class.forName("oracle.jdbc.OracleDriver");
            Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
            System.out.println("Oracle JDBC Driver loaded and registered successfully.");
        } catch (Throwable e) {
            System.err.println("CRITICAL: Failed to register Oracle JDBC Driver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
