package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ManageSharedTripController {

    @FXML
    private Label tripTitleLabel;
    @FXML
    private TextField uidField;
    @FXML
    private ComboBox<String> permissionBox;
    @FXML
    private ListView<String> sharedUsersListView;

    private Trip trip;

    @FXML
    public void initialize() {
        permissionBox.getItems().addAll(
                "VIEW ONLY",
                "CAN EDIT"
        );
        permissionBox.setValue("VIEW ONLY");
        // PLACEHOLDER DATA - In a real app, this would come from the database based on the selected trip
        sharedUsersListView.getItems().add(
                "USR11111 | VIEW ONLY"
        );
        sharedUsersListView.getItems().add(
                "USR22222 | CAN EDIT"
        );
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        tripTitleLabel.setText(
                "Manage Sharing: " +
                        trip.getName()
        );
    }

    @FXML
    private void shareTrip() {
        String uid = uidField.getText();
        String permission =
                permissionBox.getValue();
        if (uid.isEmpty()) {
            return;
        }
        sharedUsersListView.getItems().add(
                uid + " | " + permission
        );
        uidField.clear();
    }

    @FXML
    private void togglePermission() {
        String selected =
                sharedUsersListView
                        .getSelectionModel()
                        .getSelectedItem();
        if (selected == null) {
            return;
        }
        if (selected.contains("VIEW ONLY")) {
            selected =
                    selected.replace(
                            "VIEW ONLY",
                            "CAN EDIT"
                    );
        } else {
            selected =
                    selected.replace(
                            "CAN EDIT",
                            "VIEW ONLY"
                    );
        }
        int index =
                sharedUsersListView
                        .getSelectionModel()
                        .getSelectedIndex();
        sharedUsersListView
                .getItems()
                .set(index, selected);
    }

    @FXML
    private void removeAccess() {

        String selected =
                sharedUsersListView
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {
            return;
        }

        sharedUsersListView
                .getItems()
                .remove(selected);
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/SharedTrips.fxml"
                            )
                    );
            Stage stage =
                    (Stage) sharedUsersListView
                            .getScene()
                            .getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
