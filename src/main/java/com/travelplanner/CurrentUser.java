package com.travelplanner;

public class CurrentUser {

    private static String email;

    public static void setEmail(String e) {
        email = e;
    }

    public static String getEmail() {
        return email;
    }
}
