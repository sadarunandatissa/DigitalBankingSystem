package com.bank.ui;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.AuditLogger;
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

    @FXML private TabPane tabPane;
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
        setupTableColumns();
        refreshAll();
    }

    @FXML
    private void initialize() {
        // Additional initialization if needed
    }

    private void setupTableColumns() {
        // Accounts
        colAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // Transactions
        colTransId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colTransType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTransAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colTransDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp().toString()));
        colTransAccount.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // Loans
        colLoanId.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colLoanRate.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("termMonths"));
        colLoanMonthly.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        colLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Bills
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
        notificationArea.setText(NotificationService.getNotifications(customer));
    }

    @FXML
    private void deposit() {
        Account selected = showAccountChoiceDialog("Select account to deposit into:");
        if (selected == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Enter amount to deposit:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                selected.deposit(amount);
                customer.addTransaction(new Transaction("DEPOSIT", amount, selected));
                refreshAll();
                showInfo("Deposit successful. New balance: $" + selected.getBalance());
            } catch (NumberFormatException e) {
                showError("Invalid amount.");
            } catch (Exception e) {
                showError(e.getMessage());
                AuditLogger.log("Deposit failed: " + e.getMessage());
            }
        });
    }

    @FXML
    private void withdraw() {
        Account selected = showAccountChoiceDialog("Select account to withdraw from:");
        if (selected == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdrawal");
        dialog.setHeaderText("Enter amount to withdraw:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                selected.withdraw(amount);
                customer.addTransaction(new Transaction("WITHDRAWAL", amount, selected));
                refreshAll();
                showInfo("Withdrawal successful. New balance: $" + selected.getBalance());
            } catch (NumberFormatException e) {
                showError("Invalid amount.");
            } catch (Exception e) {
                showError(e.getMessage());
                AuditLogger.log("Withdrawal failed: " + e.getMessage());
            }
        });
    }

    @FXML
    private void transfer() {
        if (customer.getAccounts().size() < 2) {
            showError("You need at least two accounts to transfer.");
            return;
        }
        Account source = showAccountChoiceDialog("Select source account:");
        if (source == null) return;
        Account dest = showAccountChoiceDialog("Select destination account:");
        if (dest == null) return;
        if (source == dest) {
            showError("Cannot transfer to the same account.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Transfer");
        dialog.setHeaderText("Enter amount to transfer:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                BankService.transfer(source, dest, amount);
                customer.addTransaction(new Transaction("TRANSFER_OUT", amount, source));
                customer.addTransaction(new Transaction("TRANSFER_IN", amount, dest));
                refreshAll();
                showInfo("Transfer successful.");
            } catch (NumberFormatException e) {
                showError("Invalid amount.");
            } catch (Exception e) {
                showError(e.getMessage());
                AuditLogger.log("Transfer failed: " + e.getMessage());
            }
        });
    }

    @FXML
    private void applyForLoan() {
        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Loan Application");
        amountDialog.setHeaderText("Loan amount:");
        amountDialog.showAndWait().ifPresent(amountStr -> {
            TextInputDialog rateDialog = new TextInputDialog();
            rateDialog.setHeaderText("Interest rate (%):");
            rateDialog.showAndWait().ifPresent(rateStr -> {
                TextInputDialog termDialog = new TextInputDialog();
                termDialog.setHeaderText("Term (months):");
                termDialog.showAndWait().ifPresent(termStr -> {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        double rate = Double.parseDouble(rateStr);
                        int months = Integer.parseInt(termStr);
                        LoanService.applyForLoan(customer, amount, rate, months);
                        refreshLoans();
                        showInfo("Loan application submitted.");
                    } catch (NumberFormatException e) {
                        showError("Invalid input.");
                    }
                });
            });
        });
    }

    @FXML
    private void payBill() {
        BillPayment selected = billsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a bill first.");
            return;
        }
        if (selected.isPaid()) {
            showError("Bill already paid.");
            return;
        }
        Account account = showAccountChoiceDialog("Select account to pay from:");
        if (account == null) return;
        try {
            BillService.payBill(customer, selected, account);
            refreshAll();
            showInfo("Bill paid successfully.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) accountsTable.getScene().getWindow();
                stage.close();
                try {
                    new JavaFXMain().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Account showAccountChoiceDialog(String title) {
        ChoiceDialog<Account> dialog = new ChoiceDialog<>(customer.getAccounts().get(0), customer.getAccounts());
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setContentText("Select account:");
        return dialog.showAndWait().orElse(null);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}