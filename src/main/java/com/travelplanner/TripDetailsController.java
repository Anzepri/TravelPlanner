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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label permissionLabel;

    private Trip trip;
    private String returnPage = "/Dashboard.fxml";

    @FXML
    public void initialize() {
        for (int i = 1; i <= 12; i++) {
            hourBox.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minuteBox.getItems().add(String.format("%02d", i));
        }
        ampmBox.getItems().addAll("AM", "PM");

        itineraryListView.setOnMouseClicked(event -> {
            ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();
            if (selected == null || selected.getTitle().startsWith("===")) {
                return;
            }
            activityField.setText(selected.getTitle());
            locationField.setText(selected.getLocation());
            if (selected.getDate() != null && !selected.getDate().isEmpty()) {
                String cleanDate = selected.getDate().split(" ")[0];
                datePicker.setValue(LocalDate.parse(cleanDate));
            }
            String[] parts = selected.getTime().split("[: ]");
            if (parts.length == 3) {
                hourBox.setValue(parts[0]);
                minuteBox.setValue(parts[1]);
                ampmBox.setValue(parts[2]);
            }
        });
    }

    public void setReturnPage(String returnPage) {
        this.returnPage = returnPage;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        tripTitle.setText(trip.getName());
        destinationLabel.setText("Destination: " + trip.getDestination());
        startDateLabel.setText("Start: " + trip.getStartDate());
        endDateLabel.setText("End: " + trip.getEndDate());
        refreshGroupedList();
        setReadOnlyMode(false);
    }

    public void setReadOnlyMode(boolean readOnly) {
        activityField.setDisable(readOnly);
        locationField.setDisable(readOnly);
        datePicker.setDisable(readOnly);
        hourBox.setDisable(readOnly);
        minuteBox.setDisable(readOnly);
        ampmBox.setDisable(readOnly);
        addButton.setDisable(readOnly);
        editButton.setDisable(readOnly);
        deleteButton.setDisable(readOnly);
        permissionLabel.setText(readOnly ? "Permission: VIEW ONLY" : "Permission: CAN EDIT");
    }

    private String getSelectedTime() {
        if (hourBox.getValue() == null
                || minuteBox.getValue() == null
                || ampmBox.getValue() == null) {
            return null;
        }
        return hourBox.getValue() + ":" + minuteBox.getValue() + " " + ampmBox.getValue();
    }

    @FXML
    private void addActivity() {
        String title = UserManager.sanitize(activityField.getText());
        String location = UserManager.sanitize(locationField.getText());
        LocalDate activityDate = datePicker.getValue();
        String time = getSelectedTime();

        if (title.isEmpty()) {
            showError("Activity name is required");
            return;
        }
        if (activityDate == null) {
            showError("Please select a date");
            return;
        }
        if (time == null) {
            showError("Please select a valid time");
            return;
        }
        if (!isWithinTripDates(activityDate)) {
            showError("Activity date must be within the trip duration.");
            return;
        }

        ItineraryItem item = new ItineraryItem(
                title,
                activityDate.toString(),
                time,
                location
        );

        TripManager.addItineraryItem(trip, item);
        refreshGroupedList();
        clearInputs();
    }

    @FXML
    private void editActivity() {
        ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getTitle().startsWith("===")) {
            return;
        }

        LocalDate activityDate = datePicker.getValue();
        String time = getSelectedTime();

        if (activityDate == null) {
            showError("Please select a date");
            return;
        }
        if (time == null) {
            showError("Please select a valid time");
            return;
        }
        if (!isWithinTripDates(activityDate)) {
            showError("Activity date must be within the trip duration.");
            return;
        }

        selected.setTitle(UserManager.sanitize(activityField.getText()));
        selected.setDate(activityDate.toString());
        selected.setTime(time);
        selected.setLocation(UserManager.sanitize(locationField.getText()));
        TripManager.updateItineraryItem(selected);
        refreshGroupedList();
        clearInputs();
    }

    @FXML
    private void deleteActivity() {
        ItineraryItem selected = itineraryListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getTitle().startsWith("===")) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Activity");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this activity?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        trip.getItinerary().remove(selected);
        TripManager.deleteItineraryItem(selected.getItemId());
        refreshGroupedList();
        clearInputs();
    }

    private boolean isWithinTripDates(LocalDate activityDate) {
        LocalDate tripStart = LocalDate.parse(trip.getStartDate().split(" ")[0]);
        LocalDate tripEnd = LocalDate.parse(trip.getEndDate().split(" ")[0]);
        return !activityDate.isBefore(tripStart) && !activityDate.isAfter(tripEnd);
    }

    private void refreshGroupedList() {
        itineraryListView.getItems().clear();
        Map<String, List<ItineraryItem>> grouped = new TreeMap<>();
        for (ItineraryItem item : trip.getItinerary()) {
            grouped.computeIfAbsent(item.getDate(), k -> new ArrayList<>()).add(item);
        }
        for (String date : grouped.keySet()) {
            List<ItineraryItem> items = grouped.get(date);
            items.sort((a, b) -> parseTime(a.getTime()).compareTo(parseTime(b.getTime())));
            itineraryListView.getItems().add(new ItineraryItem("=== " + date + " ===", "", "", ""));
            itineraryListView.getItems().addAll(items);
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time.toUpperCase(), DateTimeFormatter.ofPattern("h:mm a"));
        } catch (Exception e) {
            return LocalTime.MIN;
        }
    }

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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(returnPage));
            Stage stage = (Stage) tripTitle.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
