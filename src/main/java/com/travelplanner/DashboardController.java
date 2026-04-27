package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private ListView<Trip> tripListView;

    
    @FXML
    public void initialize() {

        System.out.println("---- DASHBOARD OPENED ----");

       
        TripManager.loadTrips();

        refreshTrips();

        
        tripListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Trip selected = tripListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openTrip(selected);
                }
            }
        });
    }

   
   private void refreshTrips() {
        tripListView.getItems().clear();

        String currentUser = CurrentUser.getEmail();
        boolean isAdvisor = CurrentUser.isAdvisor(); // Check the role we stored at login

        System.out.println("LOGGED IN AS: " + currentUser + " | ROLE: " + CurrentUser.getRole());

        for (Trip t : TripManager.trips) {
            // LOGIC: Show the trip if the user is an Advisor OR if they own the trip
            if (isAdvisor || (t.getOwnerEmail() != null && t.getOwnerEmail().equals(currentUser))) {
                tripListView.getItems().add(t);
            }
        }
    }

    
    private void openTrip(Trip trip) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/TripDetails.fxml")
            );

            javafx.scene.Parent root = loader.load();

            TripDetailsController controller = loader.getController();
            controller.setTrip(trip);

            Stage stage = (Stage) tripListView.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCreateTrip(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/CreateTrip.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void handleDeleteTrip() {

    Trip selected = tripListView.getSelectionModel().getSelectedItem();

    if (selected == null) {
        System.out.println("No trip selected");
        return;
    }

    javafx.scene.control.Alert alert =
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);

    alert.setTitle("Delete Trip");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to delete this trip?");

    java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {

        TripManager.trips.remove(selected);
        TripManager.saveTrips();
        refreshTrips();

        System.out.println("Deleted trip: " + selected.getName());
    }
    }


   
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {

        CurrentUser.setEmail(null);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Login.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
