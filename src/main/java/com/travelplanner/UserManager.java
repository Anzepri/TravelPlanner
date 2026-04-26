package com.travelplanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static Map<String, String> users = new HashMap<>();
    private static final String FILE = "users.txt";

    // ensure file exists
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

    // load users
    public static void loadUsers() {
        ensureFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean register(String email, String password) {
        if (users.containsKey(email)) return false;

        users.put(email, password);

        try (FileWriter writer = new FileWriter(FILE, true)) {
            writer.write(email + "," + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean userExists(String email) {
        return users.containsKey(email);
    }

    public static boolean validate(String email, String password) {
        return users.containsKey(email) &&
               users.get(email).equals(password);
    }

    public static boolean isWrongPassword(String email, String password) {
        return users.containsKey(email) &&
               !users.get(email).equals(password);
    }
}
