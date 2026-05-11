package com.travelplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TripManager {

    public static List<Trip> trips = new ArrayList<>();

    // LOAD ALL TRIPS FOR CURRENT USER
    public static void loadTrips() {

        trips.clear();

        int currentUserId =
                UserManager.getUserIdByEmail(CurrentUser.getEmail());

        String sql = """
                SELECT *
                FROM trips
                WHERE owner_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, currentUserId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Trip trip = new Trip(
                        rs.getString("trip_name"),
                        rs.getString("destination"),
                        rs.getString("start_date"),
                        rs.getString("end_date")
                );

                trip.setOwnerEmail(CurrentUser.getEmail());
                trip.setTripId(rs.getInt("trip_id"));

                trip.setItinerary(
                        loadItineraryItems(trip.getTripId())
                );

                trips.add(trip);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to load trips: " + e.getMessage()
            );
        }
    }

    // ADD TRIP
    public static void addTrip(Trip trip) {

        int ownerId =
                UserManager.getUserIdByEmail(CurrentUser.getEmail());

        String sql = """
                INSERT INTO trips
                (owner_id, trip_name, destination, start_date, end_date)
                VALUES (?, ?, ?, ?, ?)
                RETURNING trip_id
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, ownerId);
            stmt.setString(2, trip.getName());
            stmt.setString(3, trip.getDestination());

            //Need to be SQL Dates not strings
            stmt.setDate(4, java.sql.Date.valueOf(trip.getStartDate()));
            stmt.setDate(5, java.sql.Date.valueOf(trip.getEndDate()));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                trip.setTripId(rs.getInt("trip_id"));
            }

            trips.add(trip);

        } catch (SQLException e) {

            System.out.println(
                    "Failed to add trip: " + e.getMessage()
            );
        }
    }

    // DELETE TRIP
    public static void deleteTrip(Trip trip) {

        String sql = "DELETE FROM trips WHERE trip_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, trip.getTripId());

            stmt.executeUpdate();

            trips.remove(trip);

        } catch (SQLException e) {

            System.out.println(
                    "Failed to delete trip: " + e.getMessage()
            );
        }
    }

    // ADD ITINERARY ITEM
    public static void addItineraryItem(
            Trip trip,
            ItineraryItem item
    ) {

        String sql = """
                INSERT INTO itinerary_items
                (trip_id, title, item_date, item_time, location)
                VALUES (?, ?, ?, ?, ?)
                RETURNING item_id
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, trip.getTripId());
            stmt.setString(2, item.getTitle());
            stmt.setDate(3, java.sql.Date.valueOf(item.getDate()));
            stmt.setTime(4, convertToSqlTime(item.getTime()));
            stmt.setString(5, item.getLocation());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Itinerary item added successfully with ID: " + item.getItemId());
                item.setItemId(rs.getInt("item_id"));
            }

            trip.getItinerary().add(item);

        } catch (SQLException e) {

            System.out.println(
                    "Failed to add itinerary item: " + e.getMessage()
            );
        }
    }

    // UPDATE ITINERARY ITEM
    public static void updateItineraryItem(ItineraryItem item) {

        String sql = """
                UPDATE itinerary_items
                SET title = ?,
                    item_date = ?,
                    item_time = ?,
                    location = ?
                WHERE item_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, item.getTitle());
            stmt.setDate(2, java.sql.Date.valueOf(item.getDate()));
            stmt.setTime(3, convertToSqlTime(item.getTime()));
            stmt.setString(4, item.getLocation());
            stmt.setInt(5, item.getItemId());

            stmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Failed to update itinerary item: " + e.getMessage()
            );
        }
    }

    // DELETE ITINERARY ITEM
    public static void deleteItineraryItem(int itemId) {

        String sql = """
                DELETE FROM itinerary_items
                WHERE item_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, itemId);

            stmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Failed to delete itinerary item: "
                            + e.getMessage()
            );
        }
    }

    // LOAD ITINERARY ITEMS
    public static List<ItineraryItem> loadItineraryItems(int tripId) {

        List<ItineraryItem> items = new ArrayList<>();

        String sql = """
                SELECT item_id,
                       title,
                       item_date,
                       item_time,
                       location
                FROM itinerary_items
                WHERE trip_id = ?
                ORDER BY item_date, item_time
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, tripId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ItineraryItem item = new ItineraryItem(
                        rs.getString("title"),
                        rs.getString("item_date"),
                        rs.getString("item_time"),
                        rs.getString("location")
                );

                item.setItemId(rs.getInt("item_id"));

                items.add(item);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to load itinerary: "
                            + e.getMessage()
            );
        }

        return items;
    }

    
    private static java.sql.Time convertToSqlTime(String displayTime) {
        if (displayTime == null || displayTime.isBlank()) {
            return null;
        }

        try {
            // Handles format like 03:30 PM
            java.time.LocalTime time = java.time.LocalTime.parse(
                    displayTime.toUpperCase(),
                    java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
            );
            return java.sql.Time.valueOf(time);
        } catch (Exception e1) {
            try {
                // Handles format like 3:30 PM
                java.time.LocalTime time = java.time.LocalTime.parse(
                        displayTime.toUpperCase(),
                        java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                );
                return java.sql.Time.valueOf(time);
            } catch (Exception e2) {
                try {
                    // Handles format like 15:30
                    java.time.LocalTime time = java.time.LocalTime.parse(displayTime);
                    return java.sql.Time.valueOf(time);
                } catch (Exception e3) {
                    System.out.println("Invalid time format: " + displayTime);
                    return null;
                }
            }
        }
    }

}