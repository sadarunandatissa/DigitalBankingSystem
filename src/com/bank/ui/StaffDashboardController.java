package com.bank.ui;

import com.bank.model.*;
import com.bank.service.LoanService;
import com.bank.util.BankData;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.bank.main.JavaFXMain;

public class StaffDashboardController {

    private Staff staff;
    private BankData bankData = BankData.getInstance();

    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> colUsername;
    @FXML private TableColumn<Customer, Integer> colAccountsCount;
    @FXML private TableColumn<Customer, Integer> colLoansCount;
    @FXML private TableColumn<Customer, Integer> colBillsCount;

    @FXML private TableView<Loan> pendingLoansTable;
    @FXML private TableColumn<Loan, String> colLoanId;
    @FXML private TableColumn<Loan, String> colLoanCustomer;
    @FXML private TableColumn<Loan, Double> colLoanAmount;
    @FXML private TableColumn<Loan, Double> colLoanRate;
    @FXML private TableColumn<Loan, Integer> colLoanTerm;
    @FXML private TableColumn<Loan, String> colLoanStatus;

    @FXML private TextArea logArea;

    public void setStaff(Staff staff) {
        this.staff = staff;
        setupTableColumns();
        refreshAll();
    }

    private void setupTableColumns() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAccountsCount.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAccounts().size()).asObject());
        colLoansCount.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLoans().size()).asObject());
        colBillsCount.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBills().size()).asObject());

        colLoanId.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        // Customer name – we need to store it when we add the loan; we'll set dynamically
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colLoanRate.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("termMonths"));
        colLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void refreshAll() {
        refreshCustomers();
        refreshPendingLoans();
        refreshLogs();
    }

    private void refreshCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList(bankData.getAllCustomers());
        customersTable.setItems(customers);
    }

    private void refreshPendingLoans() {
        ObservableList<Loan> pendingLoans = FXCollections.observableArrayList();
        for (Customer c : bankData.getAllCustomers()) {
            for (Loan l : c.getLoans()) {
                if (l.getStatus().equals("PENDING")) {
                    pendingLoans.add(l);
                }
            }
        }
        pendingLoansTable.setItems(pendingLoans);
        // Set customer name in table dynamically
        colLoanCustomer.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            for (Customer c : bankData.getAllCustomers()) {
                if (c.getLoans().contains(loan)) {
                    return new SimpleStringProperty(c.getUsername());
                }
            }
            return new SimpleStringProperty("Unknown");
        });
    }

    private void refreshLogs() {
        try {
            java.nio.file.Path logPath = java.nio.file.Paths.get("logs/invalid_ops.log");
            if (java.nio.file.Files.exists(logPath)) {
                String logs = String.join("\n", java.nio.file.Files.readAllLines(logPath));
                logArea.setText(logs);
            } else {
                logArea.setText("No logs found.");
            }
        } catch (Exception e) {
            logArea.setText("Could not read logs: " + e.getMessage());
        }
    }

    @FXML
    private void approveLoan() {
        Loan selected = pendingLoansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a loan first.");
            return;
        }
        if (LoanService.approveLoan(selected)) {
            refreshPendingLoans();
            showAlert("Loan approved.");
        } else {
            showAlert("Loan cannot be approved.");
        }
    }

    @FXML
    private void rejectLoan() {
        Loan selected = pendingLoansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a loan first.");
            return;
        }
        if (LoanService.rejectLoan(selected)) {
            refreshPendingLoans();
            showAlert("Loan rejected.");
        } else {
            showAlert("Loan cannot be rejected.");
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) customersTable.getScene().getWindow();
                stage.close();
                try {
                    new JavaFXMain().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}