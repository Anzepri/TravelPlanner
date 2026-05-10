package com.travelplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SharedTripManager {

    public static boolean shareTrip(
            int tripId,
            int ownerId,
            String friendUID,
            boolean canEdit
    ) {

        int sharedWithId = FriendManager.getUserIdByUID(friendUID);

        if (sharedWithId == -1) {
            return false;
        }

        String sql =
                """
                INSERT INTO shared_trips
                (trip_id, owner_id, shared_with_id, can_edit)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, tripId);
            stmt.setInt(2, ownerId);
            stmt.setInt(3, sharedWithId);
            stmt.setBoolean(4, canEdit);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Failed to share trip: " + e.getMessage());
        }

        return false;
    }

    public static ObservableList<String> loadSharedUsers(int tripId) {

        ObservableList<String> users =
                FXCollections.observableArrayList();

        String sql =
                """
                SELECT shared_with_id, can_edit
                FROM shared_trips
                WHERE trip_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, tripId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int userId = rs.getInt("shared_with_id");
                boolean canEdit = rs.getBoolean("can_edit");

                String uid =
                        FriendManager.getUIDByUserId(userId);

                String permission =
                        canEdit ? "CAN EDIT" : "VIEW ONLY";

                users.add(uid + " | " + permission);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to load shared users: " + e.getMessage()
            );
        }

        return users;
    }

    public static boolean togglePermission(int tripId, String uid) {

        int userId = FriendManager.getUserIdByUID(uid);

        if (userId == -1) {
            return false;
        }

        String sql =
                """
                UPDATE shared_trips
                SET can_edit = NOT can_edit
                WHERE trip_id = ?
                AND shared_with_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, tripId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Failed to toggle permission: " + e.getMessage()
            );
        }

        return false;
    }

    public static boolean removeAccess(int tripId, String uid) {

        int userId = FriendManager.getUserIdByUID(uid);

        if (userId == -1) {
            return false;
        }

        String sql =
                """
                DELETE FROM shared_trips
                WHERE trip_id = ?
                AND shared_with_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, tripId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Failed to remove access: " + e.getMessage()
            );
        }

        return false;
    }

    public static ObservableList<String> loadSharedTripsForUser(
            int currentUserId
    ) {

        ObservableList<String> trips =
                FXCollections.observableArrayList();

        String sql =
                """
                SELECT t.trip_name,
                       u.uid,
                       st.can_edit
                FROM shared_trips st

                JOIN trips t
                    ON st.trip_id = t.trip_id

                JOIN users u
                    ON st.owner_id = u.user_id

                WHERE st.shared_with_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, currentUserId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String tripName = rs.getString("trip_name");
                String ownerUID = rs.getString("uid");

                boolean canEdit =
                        rs.getBoolean("can_edit");

                String permission =
                        canEdit ? "CAN EDIT" : "VIEW ONLY";

                trips.add(
                        tripName
                        + " | Owner: "
                        + ownerUID
                        + " | "
                        + permission
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to load shared trips: " + e.getMessage()
            );
        }

        return trips;
    }

    public static ObservableList<String> loadTripsSharedByUser(
            int ownerId
    ) {

        ObservableList<String> trips =
                FXCollections.observableArrayList();

        String sql =
                """
                SELECT t.trip_name,
                       u.uid,
                       st.can_edit
                FROM shared_trips st

                JOIN trips t
                    ON st.trip_id = t.trip_id

                JOIN users u
                    ON st.shared_with_id = u.user_id

                WHERE st.owner_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, ownerId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String tripName = rs.getString("trip_name");
                String sharedUID = rs.getString("uid");

                boolean canEdit =
                        rs.getBoolean("can_edit");

                String permission =
                        canEdit ? "CAN EDIT" : "VIEW ONLY";

                trips.add(
                        tripName
                        + " | Shared With: "
                        + sharedUID
                        + " | "
                        + permission
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to load shared-by-user trips: "
                    + e.getMessage()
            );
        }

        return trips;
    }

    public static Trip getSharedTripByDisplayText(
            String displayText
    ) {

        String tripName =
                displayText.split(" \\| ")[0];

        String sql =
                """
                SELECT *
                FROM trips
                WHERE trip_name = ?
                LIMIT 1
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, tripName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Trip trip =
                        new Trip(
                                rs.getString("trip_name"),
                                rs.getString("destination"),
                                rs.getString("start_date"),
                                rs.getString("end_date")
                        );

                trip.setTripId(rs.getInt("trip_id"));

                trip.setItinerary(
                        TripManager.loadItineraryItems(
                                trip.getTripId()
                        )
                );

                return trip;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to load shared trip: "
                    + e.getMessage()
            );
        }

        return null;
    }
}