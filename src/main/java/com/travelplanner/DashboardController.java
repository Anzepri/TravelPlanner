package com.travelplanner;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private FlowPane tripFlowPane;
    @FXML
    private Button manageUsersButton;

    private Trip selectedTrip;
    private Node selectedCard;

    @FXML
    public void initialize() {
        System.out.println("---- DASHBOARD OPENED ----");

        TripManager.loadTrips();
        refreshTrips();

        manageUsersButton.setVisible(CurrentUser.isAdvisor());
        manageUsersButton.setManaged(CurrentUser.isAdvisor());
    }

    private void refreshTrips() {
        tripFlowPane.getChildren().clear();
        selectedTrip = null;
        selectedCard = null;

        String currentUser = CurrentUser.getEmail();
        boolean isAdvisor = CurrentUser.isAdvisor();

        System.out.println(
                "LOGGED IN AS: " + currentUser + " | ROLE: " + CurrentUser.getRole()
        );

        for (Trip trip : TripManager.trips) {
            if (isAdvisor
                    || (trip.getOwnerEmail() != null
                    && trip.getOwnerEmail().equals(currentUser))) {
                addTripCard(trip);
            }
        }
    }

    private void addTripCard(Trip trip) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/TripCard.fxml")
            );
            Node card = loader.load();
            TripCardController controller = loader.getController();
            controller.setTrip(trip);

            card.setOnMouseClicked(event -> {
                selectTripCard(trip, card);
                if (event.getClickCount() == 2) {
                    openTrip(trip);
                }
            });

            tripFlowPane.getChildren().add(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectTripCard(Trip trip, Node card) {
        if (selectedCard != null) {
            selectedCard.getStyleClass().remove("trip-card-selected");
        }

        selectedTrip = trip;
        selectedCard = card;

        if (!card.getStyleClass().contains("trip-card-selected")) {
            card.getStyleClass().add("trip-card-selected");
        }
    }

    private void openTrip(Trip trip) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/TripDetails.fxml")
            );
            Parent root = loader.load();
            TripDetailsController controller = loader.getController();
            controller.setTrip(trip);

            Stage stage = (Stage) tripFlowPane.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCreateTrip(javafx.event.ActionEvent event) {
        loadPage(event, "/CreateTrip.fxml");
    }

    @FXML
    private void goToUserManagement(javafx.event.ActionEvent event) {
        if (!CurrentUser.isAdvisor()) {
            return;
        }
        loadPage(event, "/UserManagement.fxml");
    }

    @FXML
    private void handleDeleteTrip() {
        Trip selected = selectedTrip;

        if (selected == null) {
            System.out.println("No trip selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Trip");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this trip?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TripManager.deleteTrip(selected);
            refreshTrips();
            System.out.println("Deleted trip: " + selected.getName());
        }
    }

    @FXML
    private void goToSharedTrips(javafx.event.ActionEvent event) {
        loadPage(event, "/SharedTrips.fxml");
    }

    @FXML
    private void manageSharing() {
        Trip selected = selectedTrip;
        if (selected == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ManageSharedTrip.fxml")
            );
            Stage stage = (Stage) tripFlowPane.getScene().getWindow();
            stage.getScene().setRoot(loader.load());

            ManageSharedTripController controller = loader.getController();
            controller.setTrip(selected);
            controller.setReturnPage("/Dashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        loadPage(event, "/MainDashboard.fxml");
    }

    private void loadPage(javafx.event.ActionEvent event, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
