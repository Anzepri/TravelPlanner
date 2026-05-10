package com.travelplanner;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
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
    public void initialize() {
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            LocalDate endDate = endDatePicker.getValue();

            if (newDate != null && endDate != null && endDate.isBefore(newDate)) {
                endDatePicker.setValue(null);
            }
        });

        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate startDate = startDatePicker.getValue();
                boolean invalidEndDate = startDate != null && date.isBefore(startDate);

                setDisable(empty || invalidEndDate);
                if (invalidEndDate) {
                    setStyle("-fx-opacity: 0.35;");
                }
            }
        });
    }

    @FXML
    private void handleCreateTrip(javafx.event.ActionEvent event) {
        String name = nameField.getText().trim();
        String destination = destinationField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (name.isEmpty() || destination.isEmpty()
                || startDate == null || endDate == null) {
            showError("Please fill in all fields.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showError("End date cannot be before start date.");
            return;
        }

        Trip trip = new Trip(
                name,
                destination,
                startDate.toString(),
                endDate.toString()
        );
        trip.setOwnerEmail(CurrentUser.getEmail());
        TripManager.addTrip(trip);

        loadDashboard(event);
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        loadDashboard(event);
    }

    private void loadDashboard(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Dashboard.fxml")
            );
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
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
