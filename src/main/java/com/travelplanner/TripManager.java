package com.travelplanner;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TripManager {

    public static List<Trip> trips = new ArrayList<>();

    public static void loadTrips() {
        trips.clear();

        String currentEmail = CurrentUser.getEmail();

        if (currentEmail == null) {
            return;
        }

        int ownerId = UserManager.getUserIdByEmail(currentEmail);

        String sql = """
                SELECT trip_id, trip_name, destination, start_date, end_date
                FROM trips
                WHERE owner_id = ?
                ORDER BY start_date
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Trip trip = new Trip(
                        rs.getString("trip_name"),
                        rs.getString("destination"),
                        rs.getDate("start_date").toString(),
                        rs.getDate("end_date").toString()
                );

                trip.setTripId(rs.getInt("trip_id"));
                trip.setOwnerEmail(currentEmail);

                loadItineraryForTrip(trip);

                trips.add(trip);
            }

        } catch (SQLException e) {
            System.out.println("Failed to load trips: " + e.getMessage());
        }
    }

    public static void addTrip(Trip trip) {
        String currentEmail = CurrentUser.getEmail();
        int ownerId = UserManager.getUserIdByEmail(currentEmail);

        String sql = """
                INSERT INTO trips (owner_id, trip_name, destination, start_date, end_date)
                VALUES (?, ?, ?, ?, ?)
                RETURNING trip_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            stmt.setString(2, trip.getName());
            stmt.setString(3, trip.getDestination());
            stmt.setDate(4, Date.valueOf(trip.getStartDate()));
            stmt.setDate(5, Date.valueOf(trip.getEndDate()));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                trip.setTripId(rs.getInt("trip_id"));
            }

            trip.setOwnerEmail(currentEmail);
            trips.add(trip);

        } catch (SQLException e) {
            System.out.println("Failed to add trip: " + e.getMessage());
        }
    }

    public static void deleteTrip(Trip trip) {
        String sql = "DELETE FROM trips WHERE trip_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trip.getTripId());
            stmt.executeUpdate();

            trips.remove(trip);

        } catch (SQLException e) {
            System.out.println("Failed to delete trip: " + e.getMessage());
        }
    }

    public static void addItineraryItem(Trip trip, ItineraryItem item) {
        String sql = """
                INSERT INTO itinerary_items (trip_id, title, item_date, item_time, location)
                VALUES (?, ?, ?, ?, ?)
                RETURNING item_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("Saving itinerary item...");
            System.out.println("Trip ID: " + trip.getTripId());
            System.out.println("Title: " + item.getTitle());
            System.out.println("Date: " + item.getDate());
            System.out.println("Time: " + item.getTime());
            System.out.println("Location: " + item.getLocation());

            stmt.setInt(1, trip.getTripId());
            stmt.setString(2, item.getTitle());
            stmt.setDate(3, Date.valueOf(item.getDate()));
            stmt.setTime(4, convertToSqlTime(item.getTime()));
            stmt.setString(5, item.getLocation());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item.setItemId(rs.getInt("item_id"));
                System.out.println("Saved itinerary item with ID: " + item.getItemId());
            }

            trip.getItinerary().add(item);

        } catch (SQLException e) {
            System.out.println("Failed to add itinerary item: " + e.getMessage());
        }
    }

    public static void updateItineraryItem(ItineraryItem item) {
        String sql = """
                UPDATE itinerary_items
                SET title = ?, item_date = ?, item_time = ?, location = ?
                WHERE item_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getTitle());
            stmt.setDate(2, Date.valueOf(item.getDate()));
            stmt.setTime(3, convertToSqlTime(item.getTime()));
            stmt.setString(4, item.getLocation());
            stmt.setInt(5, item.getItemId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to update itinerary item: " + e.getMessage());
        }
    }

    public static void deleteItineraryItem(Trip trip, ItineraryItem item) {
        String sql = "DELETE FROM itinerary_items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getItemId());
            stmt.executeUpdate();

            trip.getItinerary().remove(item);

        } catch (SQLException e) {
            System.out.println("Failed to delete itinerary item: " + e.getMessage());
        }
    }

    private static void loadItineraryForTrip(Trip trip) {
        String sql = """
                SELECT item_id, title, item_date, item_time, location
                FROM itinerary_items
                WHERE trip_id = ?
                ORDER BY item_date, item_time
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trip.getTripId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItineraryItem item = new ItineraryItem(
                        rs.getString("title"),
                        rs.getDate("item_date").toString(),
                        convertToDisplayTime(rs.getTime("item_time")),
                        rs.getString("location")
                );

                item.setItemId(rs.getInt("item_id"));

                trip.getItinerary().add(item);
            }

        } catch (SQLException e) {
            System.out.println("Failed to load itinerary: " + e.getMessage());
        }
    }

    private static Time convertToSqlTime(String displayTime) {
        LocalTime time = LocalTime.parse(
                displayTime.toUpperCase(),
                DateTimeFormatter.ofPattern("hh:mm a")
        );

        return Time.valueOf(time);
    }

    private static String convertToDisplayTime(Time sqlTime) {
        LocalTime time = sqlTime.toLocalTime();

        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static void saveTrips() {
        // Not needed anymore. PostgreSQL saves changes immediately.
    }
}