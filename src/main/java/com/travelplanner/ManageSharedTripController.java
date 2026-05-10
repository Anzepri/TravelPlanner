package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ManageSharedTripController {

    @FXML
    private Label tripTitleLabel;
    @FXML
    private TextField uidField;
    @FXML
    private ComboBox<String> permissionBox;
    @FXML
    private ListView<String> sharedUsersListView;
    
    private Trip trip;
    private String returnPage = "/Dashboard.fxml";
    public void setReturnPage(String returnPage) {
    this.returnPage = returnPage;
    }
   
    @FXML
    public void initialize() {
        permissionBox.getItems().addAll("VIEW ONLY","CAN EDIT");
        permissionBox.setValue("VIEW ONLY");
        // PLACEHOLDER DATA - In a real app, this would come from the database based on the selected trip
        sharedUsersListView.getItems().add("USR11111 | VIEW ONLY");
        sharedUsersListView.getItems().add("USR22222 | CAN EDIT");
        }

    public void setTrip(Trip trip) {this.trip = trip;
        tripTitleLabel.setText("Manage Sharing: " +trip.getName());
        sharedUsersListView.setItems(SharedTripManager.loadSharedUsers(trip.getTripId()));
    }

    @FXML
    private void shareTrip() {
        String uid =uidField.getText().trim().toUpperCase();
        if (uid.isEmpty()) {
            return;
            }
        boolean canEdit =permissionBox.getValue().equals("CAN EDIT");
        int ownerId =UserManager.getUserIdByEmail(CurrentUser.getEmail());
        boolean success =SharedTripManager.shareTrip(trip.getTripId(),ownerId,uid,canEdit);
        if (success) {
            uidField.clear();
            sharedUsersListView.setItems(SharedTripManager.loadSharedUsers(trip.getTripId()));
            System.out.println("Trip shared successfully");
        } else {
        System.out.println("Failed to share trip");
        }
    }

    @FXML
    private void togglePermission() {
    String selected = sharedUsersListView.getSelectionModel().getSelectedItem();

    if (selected == null) {
        return;
    }

    String uid = selected.split(" \\| ")[0];
    boolean success = SharedTripManager.togglePermission(trip.getTripId(),uid);

    if (success) {
        sharedUsersListView.setItems(SharedTripManager.loadSharedUsers(trip.getTripId()));
        }
    }

    @FXML
    private void removeAccess() {
    String selected =
            sharedUsersListView.getSelectionModel().getSelectedItem();

    if (selected == null) {
        return;
    }

    String uid = selected.split(" \\| ")[0];
    boolean success = SharedTripManager.removeAccess(trip.getTripId(),uid);
    
    if (success) {
        sharedUsersListView.setItems(
                SharedTripManager.loadSharedUsers(trip.getTripId()));
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(returnPage));
            Stage stage =(Stage) sharedUsersListView.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
