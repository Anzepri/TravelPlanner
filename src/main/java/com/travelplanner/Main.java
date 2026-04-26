package com.travelplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

    UserManager.loadUsers();
    TripManager.loadTrips();

    FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/Login.fxml")
    );

    Scene scene = new Scene(loader.load(), 900, 600);

    stage.setTitle("Travel Planner");
    stage.setScene(scene);
    stage.setResizable(true);
    stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
