package com.travelplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    public static void loadUsers() {
        // Not needed anymore because users are loaded from PostgreSQL.
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

    private static void saveUsers() {
        ensureFile();

        try (FileWriter writer = new FileWriter(FILE, false)) {
            for (String email : new TreeMap<>(users).keySet()) {
                writer.write(email + "," + users.get(email) + "," + roles.getOrDefault(email, "USER") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
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