package com.bank.ui;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;

public class CustomerDashboardController {

    private Customer customer;

    // UI elements
    @FXML private TextArea notificationArea;

    // We'll use TableView for each tab – for simplicity we'll show notifications first
    // But we'll add tables as needed.

    public void setCustomer(Customer customer) {
        this.customer = customer;
        refreshAll();
    }

    @FXML
    private void refreshAll() {
        refreshNotifications();
        // You can also refresh accounts/transactions here later
    }

    private void refreshNotifications() {
        StringBuilder sb = new StringBuilder();
        // Low balance alerts
        for (Account acc : customer.getAccounts()) {
            if (acc.getBalance() < 100) {
                sb.append("Low balance in account ").append(acc.getAccountNumber())
                  .append(": $").append(acc.getBalance()).append("\n");
            }
        }
        // Loan reminders
        for (Loan loan : customer.getLoans()) {
            if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("ACTIVE")) {
                if (loan.getNextDueDate().isBefore(LocalDate.now().plusDays(3))) {
                    sb.append("Loan ").append(loan.getLoanId())
                      .append(" payment due on ").append(loan.getNextDueDate()).append("\n");
                }
            }
        }
        // Bill reminders
        for (BillPayment bill : customer.getBills()) {
            if (!bill.isPaid() && bill.getDueDate().isBefore(LocalDate.now().plusDays(3))) {
                sb.append("Bill ").append(bill.getBillId())
                  .append(" (").append(bill.getType()).append(") due on ").append(bill.getDueDate()).append("\n");
            }
        }
        if (sb.length() == 0) sb.append("No new notifications.");
        notificationArea.setText(sb.toString());
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) notificationArea.getScene().getWindow();
                stage.close();
                // Relaunch login
                try {
                    new JavaFXMain().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // You can add methods for deposit, withdraw, transfer, loan, bill using dialogs similar to Swing
    // Example: deposit() – you can reuse the same logic from Swing, but using JavaFX dialogs
}