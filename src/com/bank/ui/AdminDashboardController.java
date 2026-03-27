package com.bank.ui;

import com.bank.model.Admin;
import com.bank.util.BankData;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AdminDashboardController {
    private Admin admin;
    @FXML private TextArea logArea;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        refreshAll();
    }

    @FXML
    private void refreshAll() {
        // Show logs
        try {
            java.nio.file.Path logPath = java.nio.file.Paths.get("logs/invalid_ops.log");
            if (java.nio.file.Files.exists(logPath)) {
                String logs = String.join("\n", java.nio.file.Files.readAllLines(logPath));
                logArea.setText(logs);
            } else {
                logArea.setText("No logs found.");
            }
        } catch (Exception e) {
            logArea.setText("Could not read log file: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION, "Logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                Stage stage = (Stage) logArea.getScene().getWindow();
                stage.close();
                try {
                    new JavaFXMain().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}