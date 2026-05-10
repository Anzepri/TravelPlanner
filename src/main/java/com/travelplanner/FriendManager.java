package com.travelplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FriendManager {
    public static int getUserIdByUID(String uid) {
    String sql =
            "SELECT user_id FROM users WHERE uid = ?";
    try (
            Connection conn =DatabaseConnection.getConnection();
            PreparedStatement stmt =conn.prepareStatement(sql)
    ) {
        stmt.setString(1, uid);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
    } catch (SQLException e) {
        System.out.println("Failed to get user by UID: "+ e.getMessage());
    }
    return -1;
    }

    public static String getUIDByUserId(int userId) {
    String sql ="SELECT uid FROM users WHERE user_id = ?";
    try (Connection conn =DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("uid");
        }
    } catch (SQLException e) {
        System.out.println("Failed to get UID: " + e.getMessage());
    }
    return null;
    }

    public static boolean sendFriendRequest(int senderId,String receiverUID) {
    int receiverId = getUserIdByUID(receiverUID);
    if (receiverId == -1) {
        return false;
    }
    String sql =
            """
            INSERT INTO friend_requests
            (sender_id, receiver_id, status)
            VALUES (?, ?, 'PENDING')
            """;
    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt =conn.prepareStatement(sql)
        ) {
        stmt.setInt(1, senderId);
        stmt.setInt(2, receiverId);
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println("Failed to send request: " + e.getMessage());

    }
    return false;
    }

        public static boolean acceptRequest(
        int currentUserId,
        String senderUID
    ) {
    int senderId = getUserIdByUID(senderUID);
    if (senderId == -1) {
        return false;
    }
    String updateRequestSQL =
            """
            UPDATE friend_requests
            SET status = 'ACCEPTED'
            WHERE sender_id = ?
            AND receiver_id = ?
            """;
    String insertFriendSQL =
            """
            INSERT INTO friends
            (user1_id, user2_id)
            VALUES (?, ?)
            """;
    try (
            Connection conn = DatabaseConnection.getConnection()
    ) {
        // UPDATE REQUEST STATUS
        PreparedStatement updateStmt = conn.prepareStatement(updateRequestSQL);
        updateStmt.setInt(1, senderId);
        updateStmt.setInt(2, currentUserId);
        updateStmt.executeUpdate();
        PreparedStatement insertStmt = conn.prepareStatement(insertFriendSQL);
        insertStmt.setInt(1, senderId);
        insertStmt.setInt(2, currentUserId);
        insertStmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println(
                "Failed to accept request: "+ e.getMessage());
    }
    return false;
    }

    public static ObservableList<String>
    loadIncomingRequests(int currentUserId) {
    ObservableList<String> requests = FXCollections.observableArrayList();
    String sql =
            """
            SELECT sender_id
            FROM friend_requests
            WHERE receiver_id = ?
            AND status = 'PENDING'
            """;
    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, currentUserId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int senderId =
                    rs.getInt("sender_id");
            String uid =
                    getUIDByUserId(senderId);
            requests.add(uid);
        }
    } catch (SQLException e) {
        System.out.println(
                "Failed to load requests: " + e.getMessage());
    }
    return requests;
    }

    public static ObservableList<String>
    loadFriendsList(int currentUserId) {
    ObservableList<String> friends = FXCollections.observableArrayList();
    String sql =
            """
            SELECT user1_id, user2_id
            FROM friends
            WHERE user1_id = ?
            OR user2_id = ?
            """;
    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, currentUserId);
        stmt.setInt(2, currentUserId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int user1 =
                    rs.getInt("user1_id");
            int user2 =
                    rs.getInt("user2_id");
            int friendId;
            // DETERMINE WHICH USER IS THE FRIEND
            if (user1 == currentUserId) {
                friendId = user2;
            } else {
                friendId = user1;
            }
            String friendUID =
                    getUIDByUserId(friendId);
            friends.add(friendUID);
        }
    } catch (SQLException e) {
        System.out.println(
                "Failed to load friends: " + e.getMessage()
        );
    }
    return friends;
    }
}

