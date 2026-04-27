package com.travelplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        String email = emailField.getText().trim().toLowerCase();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password");
            return;
        }

        if (!UserManager.userExists(email)) {
            showError("User not found. Please register first.");
            return;
        }

        if (UserManager.isWrongPassword(email, password)) {
            showError("Wrong password");
            return;
        }

        if (UserManager.validate(email, password)) {
            CurrentUser.setEmail(email);
            CurrentUser.setRole(UserManager.getRole(email));

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                javafx.scene.Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRegister(javafx.event.ActionEvent event) {
        String email = emailField.getText().trim().toLowerCase();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Enter email and password to register");
            return;
        }

        if (UserManager.register(email, password)) {
            showSuccess("Registration successful! You can now log in.");
        } else {
            showError("User already exists");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}