package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    }
    @FXML
    private void sendFriendRequest() {

        System.out.println("Send request");
    }

    @FXML
    private void acceptRequest() {

        System.out.println("Accept request");
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