package com.travelplanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class UserManagementController {

    @FXML private TableView<UserRoleRow> userTable;
    @FXML private TableColumn<UserRoleRow, String> emailColumn;
    @FXML private TableColumn<UserRoleRow, String> roleColumn;
    @FXML private Button makeAdvisorButton;
    @FXML private Button makeUserButton;

    @FXML
    public void initialize() {
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleProperty());

        refreshUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            makeAdvisorButton.setDisable(!hasSelection);
            makeUserButton.setDisable(!hasSelection);
        });
    }

    private void refreshUsers() {
        var rows = FXCollections.<UserRoleRow>observableArrayList();

        UserManager.loadUsers();
        UserManager.getUsersWithRoles().forEach((email, role) -> rows.add(new UserRoleRow(email, role)));

        userTable.setItems(rows);
        makeAdvisorButton.setDisable(true);
        makeUserButton.setDisable(true);
    }

    @FXML
    private void handleMakeAdvisor() {
        updateSelectedUserRole("TRIP_ADVISOR");
    }

    @FXML
    private void handleMakeUser() {
        UserRoleRow selected = userTable.getSelectionModel().getSelectedItem();

        if (selected != null && selected.getEmail().equals(CurrentUser.getEmail())) {
            showMessage(Alert.AlertType.ERROR, "You cannot remove your own advisor role while logged in.");
            return;
        }

        updateSelectedUserRole("USER");
    }

    private void updateSelectedUserRole(String role) {
        UserRoleRow selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage(Alert.AlertType.ERROR, "Select a user first.");
            return;
        }

        if (UserManager.updateRole(selected.getEmail(), role)) {
            selected.setRole(role);
            userTable.refresh();
            showMessage(Alert.AlertType.INFORMATION, "Updated " + selected.getEmail() + " to " + role + ".");
        } else {
            showMessage(Alert.AlertType.ERROR, "Could not update that user's role.");
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class UserRoleRow {
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;

        public UserRoleRow(String email, String role) {
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
        }

        public String getEmail() {
            return email.get();
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }

        public SimpleStringProperty roleProperty() {
            return role;
        }

        public void setRole(String role) {
            this.role.set(role);
        }
    }
}
