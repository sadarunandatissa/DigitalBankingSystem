package com.bank.main;

import com.bank.util.BankData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load data (same as before)
        try {
            BankData.getInstance().loadData();
        } catch (Exception e) {
            System.out.println("No existing data found.");
        }

        // Add shutdown hook (optional, but good to keep)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BankData.getInstance().saveData();
            } catch (Exception e) {
                System.err.println("Error saving data: " + e.getMessage());
            }
        }));

        // Load the login screen
        Parent root = FXMLLoader.load(getClass().getResource("/com/bank/ui/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/bank/ui/style.css").toExternalForm());
        primaryStage.setTitle("Digital Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}