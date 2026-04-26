package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

        String name = nameField.getText();
        String destination = destinationField.getText();
        String startDate = String.valueOf(startDatePicker.getValue());
        String endDate = String.valueOf(endDatePicker.getValue());

        
        if (name.isEmpty() || destination.isEmpty() ||
            startDatePicker.getValue() == null || endDatePicker.getValue() == null) {

            System.out.println("Please fill in all fields");
            return;
        }

        
        Trip trip = new Trip(name, destination, startDate, endDate);

        
        trip.setOwnerEmail(CurrentUser.getEmail());

        
        TripManager.trips.add(trip);
        TripManager.saveTrips();

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
}
