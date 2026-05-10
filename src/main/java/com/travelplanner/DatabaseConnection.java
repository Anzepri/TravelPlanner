package com.travelplanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database location and port
    private static final String URL = "jdbc:postgresql://localhost:5432/travel_planner";
    private static final String USER = "postgres";

    // REPLACE 'YourPasswordHere' with the password you set during PostgreSQL installation
    private static final String PASSWORD = "post";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
