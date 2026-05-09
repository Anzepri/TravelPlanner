package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainDashboardController {

    @FXML
    private Label emailLabel;
    @FXML
    private Label uidLabel;
    @FXML
    public void initialize() {
        emailLabel.setText(
                "Logged in as: " +
                        CurrentUser.getEmail()
        );
        uidLabel.setText(
                "UID: " +
                        UserManager.getUID(
                                CurrentUser.getEmail()
                        )
        );
    }

    @FXML
    private void openTrips() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/Dashboard.fxml"
                            )
                    );
            Stage stage =
                    (Stage) emailLabel.getScene().getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openFriends() {
    try {
    FXMLLoader loader =
            new FXMLLoader(
                    getClass().getResource("/Friends.fxml")
            );
    Stage stage =
            (Stage) emailLabel
                    .getScene()
                    .getWindow();
    stage.getScene().setRoot(loader.load());
    } catch (Exception e) {
    e.printStackTrace();
    }
    }

    @FXML
    private void openSharedTrips() {
    try {
    FXMLLoader loader =
            new FXMLLoader(
                    getClass().getResource(
                            "/SharedTrips.fxml"
                    )
            );
    Stage stage =
            (Stage) emailLabel
                    .getScene()
                    .getWindow();

    stage.getScene().setRoot(loader.load());
    } 
    catch (Exception e) {
    e.printStackTrace();
    }
    }

    @FXML
    private void handleLogout() {
    Alert confirm =
            new Alert(Alert.AlertType.CONFIRMATION);

    confirm.setTitle("Logout");
    confirm.setHeaderText(null);
    confirm.setContentText(
            "Are you sure you want to logout?"
    );
    if (confirm.showAndWait().orElse(ButtonType.CANCEL)
            != ButtonType.OK) {

        return;
    }
    try {
        CurrentUser.setEmail(null);
        CurrentUser.setRole(null);
        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/Login.fxml"
                        )
                );
        Stage stage =
                (Stage) emailLabel
                        .getScene()
                        .getWindow();
        stage.getScene().setRoot(loader.load());
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}