package com.bank.ui;

import com.bank.model.*;
import com.bank.util.BankData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private BankData bankData = BankData.getInstance();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (bankData.authenticateCustomer(username, password)) {
            loadDashboard("CustomerDashboard.fxml", bankData.getCustomer(username));
        } else if (bankData.authenticateStaff(username, password)) {
            loadDashboard("StaffDashboard.fxml", bankData.getStaff(username));
        } else if (bankData.authenticateAdmin(username, password)) {
            loadDashboard("AdminDashboard.fxml", bankData.getAdmin(username));
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    private void loadDashboard(String fxml, Object user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if (user instanceof Customer) {
                CustomerDashboardController controller = loader.getController();
                controller.setCustomer((Customer) user);
            } else if (user instanceof Staff) {
                StaffDashboardController controller = loader.getController();
                controller.setStaff((Staff) user);
            } else if (user instanceof Admin) {
                AdminDashboardController controller = loader.getController();
                controller.setAdmin((Admin) user);
            }
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + user);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
        }
    }
}