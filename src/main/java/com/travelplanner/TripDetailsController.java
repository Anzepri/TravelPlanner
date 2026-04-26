package com.travelplanner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TripDetailsController {

    @FXML private Label tripTitle;
    @FXML private Label destinationLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;

    @FXML private TextField activityField;
    @FXML private TextField locationField;

    @FXML private DatePicker datePicker;

    @FXML private ComboBox<String> hourBox;
    @FXML private ComboBox<String> minuteBox;
    @FXML private ComboBox<String> ampmBox;

    @FXML private ListView<ItineraryItem> itineraryListView;

    private Trip trip;

    // 🔹 Initialize
    @FXML
    public void initialize() {

        // hours
        for (int i = 1; i <= 12; i++) {
            hourBox.getItems().add(String.format("%02d", i));
        }

        // minutes
        for (int i = 0; i < 60; i++) {
            minuteBox.getItems().add(String.format("%02d", i));
        }

        // AM/PM
        ampmBox.getItems().addAll("AM", "PM");

        // click item → autofill
        itineraryListView.setOnMouseClicked(event -> {
            ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();

            if (selected == null || selected.getTitle().startsWith("===")) return;

            activityField.setText(selected.getTitle());
            locationField.setText(selected.getLocation());

            // date
            if (selected.getDate() != null && !selected.getDate().isEmpty()) {
                datePicker.setValue(LocalDate.parse(selected.getDate()));
            }

            // time split
            String[] parts = selected.getTime().split("[: ]");
            if (parts.length == 3) {
                hourBox.setValue(parts[0]);
                minuteBox.setValue(parts[1]);
                ampmBox.setValue(parts[2]);
            }
        });
    }

    // 🔹 Set trip
    public void setTrip(Trip trip) {
        this.trip = trip;

        tripTitle.setText(trip.getName());
        destinationLabel.setText("Destination: " + trip.getDestination());
        startDateLabel.setText("Start: " + trip.getStartDate());
        endDateLabel.setText("End: " + trip.getEndDate());

        refreshGroupedList();
    }

    // 🔹 Get selected time
    private String getSelectedTime() {
        if (hourBox.getValue() == null ||
            minuteBox.getValue() == null ||
            ampmBox.getValue() == null) {
            return null;
        }

        return hourBox.getValue() + ":" +
               minuteBox.getValue() + " " +
               ampmBox.getValue();
    }

    // 🔹 Add activity
    @FXML
    private void addActivity() {

        String title = activityField.getText();
        String date = String.valueOf(datePicker.getValue());
        String time = getSelectedTime();
        String location = locationField.getText();

        if (title == null || title.isEmpty()) {
            showError("Activity name is required");
            return;
        }

        if (time == null) {
            showError("Please select a valid time");
            return;
        }

        ItineraryItem item = new ItineraryItem(title, date, time, location);
        trip.getItinerary().add(item);
        TripManager.saveTrips();
        refreshGroupedList();

        clearInputs();
    }

    // 🔹 Edit activity
    @FXML
    private void editActivity() {

        ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();

        if (selected == null || selected.getTitle().startsWith("===")) return;

        String time = getSelectedTime();

        if (time == null) {
            showError("Please select a valid time");
            return;
        }

        selected.setTitle(activityField.getText());
        selected.setDate(String.valueOf(datePicker.getValue()));
        selected.setTime(time);
        selected.setLocation(locationField.getText());
        TripManager.saveTrips();
        refreshGroupedList();

        clearInputs();
    }

    // 🔹 Delete
    @FXML
    private void deleteActivity() {

        ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();

        if (selected == null || selected.getTitle().startsWith("===")) return;

        trip.getItinerary().remove(selected);
        TripManager.saveTrips();
        refreshGroupedList();
    }

    // 🔹 Group + sort
    private void refreshGroupedList() {

        itineraryListView.getItems().clear();

        Map<String, List<ItineraryItem>> grouped = new TreeMap<>();

        for (ItineraryItem item : trip.getItinerary()) {
            grouped.computeIfAbsent(item.getDate(), k -> new ArrayList<>())
                   .add(item);
        }

        for (String date : grouped.keySet()) {

            List<ItineraryItem> items = grouped.get(date);

            // sort by time
            items.sort((a, b) ->
                    parseTime(a.getTime()).compareTo(parseTime(b.getTime()))
            );

            itineraryListView.getItems().add(
                    new ItineraryItem("=== " + date + " ===", "", "", "")
            );

            itineraryListView.getItems().addAll(items);
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time.toUpperCase(),
                    DateTimeFormatter.ofPattern("h:mm a"));
        } catch (Exception e) {
            return LocalTime.MIN;
        }
    }

    // 🔹 Helpers
    private void clearInputs() {
        activityField.clear();
        locationField.clear();
        datePicker.setValue(null);
        hourBox.setValue(null);
        minuteBox.setValue(null);
        ampmBox.setValue(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 🔹 Back
    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Dashboard.fxml")
            );

            Stage stage = (Stage) tripTitle.getScene().getWindow();
            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}