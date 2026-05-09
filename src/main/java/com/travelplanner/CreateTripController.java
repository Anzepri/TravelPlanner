package com.travelplanner;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateTripController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField destinationField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private void handleCreateTrip(javafx.event.ActionEvent event) {

        String name = nameField.getText().trim();
        String destination = destinationField.getText().trim();

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Check empty fields
        if (name.isEmpty() || destination.isEmpty()
                || startDate == null || endDate == null) {

            showError("Please fill in all fields.");
            return;
        }

        // Validate trip dates
        if (endDate.isBefore(startDate)) {

            showError("End date cannot be before start date.");
            return;
        }

        // Create trip
        Trip trip = new Trip(
                name,
                destination,
                startDate.toString(),
                endDate.toString()
        );

        // Assign owner
        trip.setOwnerEmail(CurrentUser.getEmail());

        // Save trip
        TripManager.addTrip(trip);

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Dashboard.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}