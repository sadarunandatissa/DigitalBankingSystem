package com.bank.ui;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;

public class CustomerDashboardController {

    private Customer customer;

    // Tables and columns
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> colAccountNo;
    @FXML private TableColumn<Account, String> colType;
    @FXML private TableColumn<Account, Double> colBalance;

    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> colTransId;
    @FXML private TableColumn<Transaction, String> colTransType;
    @FXML private TableColumn<Transaction, Double> colTransAmount;
    @FXML private TableColumn<Transaction, String> colTransDate;
    @FXML private TableColumn<Transaction, String> colTransAccount;

    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, String> colLoanId;
    @FXML private TableColumn<Loan, Double> colLoanAmount;
    @FXML private TableColumn<Loan, Double> colLoanRate;
    @FXML private TableColumn<Loan, Integer> colLoanTerm;
    @FXML private TableColumn<Loan, Double> colLoanMonthly;
    @FXML private TableColumn<Loan, String> colLoanStatus;

    @FXML private TableView<BillPayment> billsTable;
    @FXML private TableColumn<BillPayment, String> colBillId;
    @FXML private TableColumn<BillPayment, String> colBillType;
    @FXML private TableColumn<BillPayment, Double> colBillAmount;
    @FXML private TableColumn<BillPayment, LocalDate> colBillDue;
    @FXML private TableColumn<BillPayment, Boolean> colBillPaid;

    @FXML private TextArea notificationArea;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        refreshAll();
    }

    @FXML
    private void initialize() {
        // Setup table columns
        colAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        colTransId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colTransType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTransAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colTransDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp().toString()));
        colTransAccount.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        colLoanId.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colLoanRate.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("termMonths"));
        colLoanMonthly.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        colLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colBillType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBillAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colBillDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colBillPaid.setCellValueFactory(new PropertyValueFactory<>("paid"));
    }

    private void refreshAll() {
        refreshAccounts();
        refreshTransactions();
        refreshLoans();
        refreshBills();
        refreshNotifications();
    }

    private void refreshAccounts() {
        ObservableList<Account> accounts = FXCollections.observableArrayList(customer.getAccounts());
        accountsTable.setItems(accounts);
    }

    private void refreshTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList(customer.getTransactions());
        transactionsTable.setItems(transactions);
    }

    private void refreshLoans() {
        ObservableList<Loan> loans = FXCollections.observableArrayList(customer.getLoans());
        loansTable.setItems(loans);
    }

    private void refreshBills() {
        ObservableList<BillPayment> bills = FXCollections.observableArrayList(customer.getBills());
        billsTable.setItems(bills);
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
    private void applyForLoan() {
        // Use dialogs to get input, similar to Swing but with JavaFX
        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Loan Application");
        amountDialog.setHeaderText("Enter loan amount:");
        amountDialog.showAndWait().ifPresent(amountStr -> {
            TextInputDialog rateDialog = new TextInputDialog();
            rateDialog.setHeaderText("Enter interest rate (%):");
            rateDialog.showAndWait().ifPresent(rateStr -> {
                TextInputDialog termDialog = new TextInputDialog();
                termDialog.setHeaderText("Enter term (months):");
                termDialog.showAndWait().ifPresent(termStr -> {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        double rate = Double.parseDouble(rateStr);
                        int months = Integer.parseInt(termStr);
                        LoanService.applyForLoan(customer, amount, rate, months);
                        refreshLoans();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Loan application submitted.");
                        alert.showAndWait();
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input.");
                        alert.showAndWait();
                    }
                });
            });
        });
    }

    @FXML
    private void payBill() {
        BillPayment selected = billsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select a bill first.");
            alert.showAndWait();
            return;
        }
        if (selected.isPaid()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Bill already paid.");
            alert.showAndWait();
            return;
        }
        // Choose account
        ChoiceDialog<Account> choiceDialog = new ChoiceDialog<>(customer.getAccounts().get(0), customer.getAccounts());
        choiceDialog.setTitle("Pay Bill");
        choiceDialog.setHeaderText("Select account to pay from:");
        choiceDialog.showAndWait().ifPresent(account -> {
            try {
                BillService.payBill(customer, selected, account);
                refreshAll();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bill paid successfully.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Payment failed: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) accountsTable.getScene().getWindow();
                stage.close();
                new JavaFXMain().start(new Stage()); // Relaunch login
            }
        });
    }

    // You can add deposit, withdraw, transfer similarly with dialogs
}