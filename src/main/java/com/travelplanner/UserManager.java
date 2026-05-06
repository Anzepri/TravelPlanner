package com.travelplanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

public class UserManager {

    private static Map<String, String> users = new HashMap<>();
    private static Map<String, String> roles = new HashMap<>();
    private static final String FILE = "users.txt";

    private static void ensureFile() {
        try {
            File file = new File(FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadUsers() {
        users.clear();
        roles.clear();
        ensureFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    users.put(parts[0], parts[1]);
                    String role = (parts.length == 3) ? parts[2] : "USER";
                    roles.put(parts[0], role);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (users.containsKey(email)) return false;

        String hashedPassword = hashPassword(password);
        String defaultRole = "USER";
        
        users.put(email, hashedPassword);
        roles.put(email, defaultRole);

        try (FileWriter writer = new FileWriter(FILE, true)) {
            writer.write(email + "," + hashedPassword + "," + defaultRole + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getRole(String email) {
        return roles.getOrDefault(email, "USER");
    }

    public static Map<String, String> getUsersWithRoles() {
        return new TreeMap<>(roles);
    }

    public static boolean updateRole(String email, String role) {
        if (!users.containsKey(email)) return false;
        if (!"USER".equals(role) && !"TRIP_ADVISOR".equals(role)) return false;

        roles.put(email, role);
        saveUsers();
        return true;
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
        return users.containsKey(email);
    }

    public static boolean validate(String email, String password) {
        if (!users.containsKey(email)) return false;
        String hashedInput = hashPassword(password);
        return users.get(email).equals(hashedInput);
    }

    public static boolean isWrongPassword(String email, String password) {
        if (!users.containsKey(email)) return false;
        String hashedInput = hashPassword(password);
        return !users.get(email).equals(hashedInput);
    }
}
