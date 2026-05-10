package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class FriendsController {
    @FXML
    private TextField uidField;
    @FXML
    private ListView<String> requestsListView;
    @FXML
    private ListView<String> friendsListView;
    @FXML
    public void initialize() {
    int currentUserId =
            UserManager.getUserIdByEmail(
                    CurrentUser.getEmail()
            );
    requestsListView.setItems(
            FriendManager.loadIncomingRequests(
                    currentUserId
            )
    );
    friendsListView.setItems(
        FriendManager.loadFriendsList(
                currentUserId
        )
    );
    }
    
    @FXML
    private void sendFriendRequest() {
        String uidInput =
                uidField.getText().trim();

        if (!uidInput.matches("\\d+")) {
                Alert alert =
                new Alert(Alert.AlertType.ERROR);

                alert.setHeaderText(null);

                alert.setContentText(
                "Please enter numbers only."
                );

                alert.showAndWait();
                return;
        }

        String uid = "USR" + uidInput;
        int senderId =
            UserManager.getUserIdByEmail(
                    CurrentUser.getEmail()
            );
         boolean success =
            FriendManager.sendFriendRequest(
                    senderId,
                    uid
            );
        if (success) {
                Alert alert =
                        new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText(
                        "Friend request sent!");
                        alert.showAndWait();
        } 
        else {
                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                        alert.setHeaderText(null);
                        alert.setContentText(
                        "Could not send friend request.");
                        alert.showAndWait();
                }
        }

    @FXML
    private void acceptRequest() {
    String selectedUID =
            requestsListView
                    .getSelectionModel()
                    .getSelectedItem();
    if (selectedUID == null) {
        return;
    }
    int currentUserId =
            UserManager.getUserIdByEmail(
                    CurrentUser.getEmail()
            );
    boolean success =
            FriendManager.acceptRequest(
                    currentUserId,
                    selectedUID
            );
    if (success) {
        requestsListView.getItems().remove(selectedUID);
        friendsListView.setItems(FriendManager.loadFriendsList(currentUserId));
        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );
        alert.setHeaderText(null);
        alert.setContentText(
                "Friend request accepted!"
        );
        alert.showAndWait();
        } else {
        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );
        alert.setHeaderText(null);
        alert.setContentText(
                "Failed to accept request."
        );
        alert.showAndWait();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/MainDashboard.fxml"
                            )
                    );
            Stage stage =
                    (Stage) uidField
                            .getScene()
                            .getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}