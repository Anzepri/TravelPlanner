package com.travelplanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TripCardController {

    @FXML
    private VBox cardRoot;

    @FXML
    private Label tripNameLabel;

    @FXML
    private Label destinationLabel;

    private Trip trip;

    public void setTrip(Trip trip) {
        this.trip = trip;
        tripNameLabel.setText(trip.getName());
        destinationLabel.setText(trip.getDestination());
    }

    public Trip getTrip() {
        return trip;
    }

    public VBox getCardRoot() {
        return cardRoot;
    }
}
