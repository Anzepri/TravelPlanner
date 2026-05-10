package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

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

        String name = nameField.getText();
        String destination = destinationField.getText();
        LocalDate startDateValue = startDatePicker.getValue();
        LocalDate endDateValue = endDatePicker.getValue();

        
        if (name.isEmpty() || destination.isEmpty() ||
            startDateValue == null || endDateValue == null) {

            showError("Please fill in all fields");
            return;
        }

        if (endDateValue.isBefore(startDateValue)) {
            showError("End date cannot be before start date.");
            return;
        }

        
        Trip trip = new Trip(name, destination, startDateValue.toString(), endDateValue.toString());

        
        trip.setOwnerEmail(CurrentUser.getEmail());

        
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
