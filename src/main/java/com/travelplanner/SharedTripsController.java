package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class SharedTripsController {

    @FXML
    private ListView<String> sharedWithMeListView;

    @FXML
    private ListView<String> sharedByMeListView;

    @FXML
    public void initialize() {
        int currentUserId = UserManager.getUserIdByEmail(CurrentUser.getEmail());

        sharedWithMeListView.setItems(
                SharedTripManager.loadSharedTripsForUser(currentUserId)
        );

        sharedByMeListView.setItems(
                SharedTripManager.loadTripsSharedByUser(currentUserId)
        );
    }

    @FXML
    private void openSharedTrip() {
        String selected = sharedWithMeListView
                .getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/TripDetails.fxml")
            );

            Stage stage = (Stage) sharedWithMeListView
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(loader.load());

            TripDetailsController controller = loader.getController();

            Trip selectedTrip =
                    SharedTripManager.getSharedTripByDisplayText(selected);

            controller.setTrip(selectedTrip);
            controller.setReturnPage("/SharedTrips.fxml");

            // Set read-only mode if applicable
            controller.setReadOnlyMode(selected.contains("VIEW ONLY"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void manageSharedTrip() {
        String selected = sharedByMeListView
                .getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ManageSharedTrip.fxml")
            );

            Stage stage = (Stage) sharedByMeListView
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(loader.load());

            ManageSharedTripController controller =
                    loader.getController();

            Trip trip =
                    SharedTripManager.getSharedTripByDisplayText(selected);

            if (trip != null) {
                controller.setTrip(trip);
                controller.setReturnPage("/SharedTrips.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/MainDashboard.fxml")
            );

            Stage stage = (Stage) sharedWithMeListView
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}