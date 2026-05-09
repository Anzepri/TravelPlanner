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

    private final java.util.Map<String, Trip> sharedTrips =
        new java.util.HashMap<>();

    @FXML //PLACEHOLDER DATA - In a real app, this would come from the database based on the logged in user
    public void initialize() {
    Trip japanTrip =
            new Trip(
                    "Japan Trip",
                    "Tokyo",
                    "2026-06-01",
                    "2026-06-10"
            );
    Trip beachTrip =
            new Trip(
                    "Beach Vacation",
                    "Hawaii",
                    "2026-07-05",
                    "2026-07-12"
            );
    sharedTrips.put(
            "Japan Trip | Owner: USR48291 | CAN EDIT",
            japanTrip
    );
    sharedTrips.put(
            "Beach Vacation | Owner: USR77777 | VIEW ONLY",
            beachTrip
    );
    sharedWithMeListView.getItems().addAll(
            sharedTrips.keySet()
    );
    }
    
   @FXML
    private void openSharedTrip() {
    String selected =
            sharedWithMeListView
                    .getSelectionModel()
                    .getSelectedItem();
    if (selected == null) {
        return;
    }
    try {
        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/TripDetails.fxml"
                        )
                );
        Stage stage =
                (Stage) sharedWithMeListView
                        .getScene()
                        .getWindow();
        stage.getScene().setRoot(loader.load());
        TripDetailsController controller =
                loader.getController();
        Trip selectedTrip =
                sharedTrips.get(selected);
        controller.setTrip(selectedTrip);
        controller.setReturnPage(
        "/SharedTrips.fxml"
        );
        // READ ONLY VS EDITABLE
        if (selected.contains("VIEW ONLY")) {
            controller.setReadOnlyMode(true);
        } else {
            controller.setReadOnlyMode(false);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @FXML
    private void manageSharedTrip() {
    String selected =
        sharedByMeListView
                .getSelectionModel()
                .getSelectedItem();

    if (selected == null) {
    return;
    }
    try {
    FXMLLoader loader =
            new FXMLLoader(
                    getClass().getResource(
                            "/ManageSharedTrip.fxml"
                    )
            );

    Stage stage =
            (Stage) sharedByMeListView
                    .getScene()
                    .getWindow();
    stage.getScene().setRoot(loader.load());

    ManageSharedTripController controller =
            loader.getController();

    Trip trip =
            new Trip(
                    "Paris Trip",
                    "France",
                    "2026-06-01",
                    "2026-06-10"
            );
    controller.setTrip(trip);
    } catch (Exception e) {
    e.printStackTrace();
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
                    (Stage) sharedWithMeListView
                            .getScene()
                            .getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}