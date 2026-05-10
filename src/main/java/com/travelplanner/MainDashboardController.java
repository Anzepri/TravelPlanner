package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainDashboardController {

    @FXML private Label emailLabel;
    @FXML private Label uidLabel;

    @FXML
    public void initialize() {
        emailLabel.setText("Logged in as: " + CurrentUser.getEmail());
        uidLabel.setText("UID: " + UserManager.getUID(CurrentUser.getEmail()));
    }

    @FXML
    private void openTrips() {
        loadPage("/Dashboard.fxml");
    }

    @FXML
    private void openFriends() {
        loadPage("/Friends.fxml");
    }

    @FXML
    private void openSharedTrips() {
        loadPage("/SharedTrips.fxml");
    }

    @FXML
    private void handleLogout() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        CurrentUser.setEmail(null);
        CurrentUser.setRole(null);

        loadPage("/Login.fxml");
    }

    private void loadPage(String fxmlFile) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlFile));

            Stage stage =
                    (Stage) emailLabel.getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}