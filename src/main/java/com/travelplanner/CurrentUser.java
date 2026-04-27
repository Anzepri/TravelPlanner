package com.travelplanner;

public class CurrentUser {

    private static String email;
    private static String role;

    public static void setEmail(String e) { email = e; }
    public static String getEmail() { return email; }

    public static void setRole(String r) { role = r; }
    public static String getRole() { return role; }

    public static boolean isAdvisor() {
        return "TRIP_ADVISOR".equalsIgnoreCase(role);
    }

    public static void clear() {
        email = null;
        role = null;
    }
}