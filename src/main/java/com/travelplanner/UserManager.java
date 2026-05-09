package com.travelplanner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

public class UserManager {

   
    public static java.util.Map<String, String> getUsersWithRoles() {

    java.util.Map<String, String> users = new java.util.HashMap<>();

    String sql = "SELECT username, role FROM users";

    try (
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        while (rs.next()) {
            users.put(
                    rs.getString("username"),
                    rs.getString("role")
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return users;
    }
    public static boolean updateRole(String email, String role) {

    String sql = "UPDATE users SET role = ? WHERE username = ?";

    try (
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setString(1, role);
        stmt.setString(2, email);

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
    }
    /**
     * Secures passwords using SHA-256 hashing.
     * Fixed: Use formatHex() for Java 17 compatibility.
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash); 
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    public static String getRole(String email) {
    return "USER"; // Default role for all users
    }

    /**
     * Prevents delimiters from breaking the file-based database.
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replace("|", "-").replace(",", " ").trim();
    }

    public static boolean register(String email, String password) {
        if (userExists(email)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean userExists(String email) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("User check failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean validate(String email, String password) {
        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean isWrongPassword(String email, String password) {
        return userExists(email) && !validate(email, password);
    }

    public static int getUserIdByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            System.out.println("Failed to get user ID: " + e.getMessage());
        }

        return -1;
    }
}