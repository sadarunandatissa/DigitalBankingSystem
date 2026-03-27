package com.bank.ui;

import com.bank.model.*;
import com.bank.util.BankData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminDashboardController {

    private Admin admin;
    private BankData bankData = BankData.getInstance();

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colDetails;

    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private TableView<ObservableList<String>> reportTable;

    @FXML private TextArea logArea;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        setupTableColumns();
        reportTypeCombo.setItems(FXCollections.observableArrayList(
            "Customer Transaction Summary",
            "Loan Performance",
            "Bill Payment Summary"
        ));
        reportTypeCombo.getSelectionModel().selectFirst();
        refreshAll();
    }

    private void setupTableColumns() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Customer) return new SimpleStringProperty("Customer");
            if (cellData.getValue() instanceof Staff) return new SimpleStringProperty("Staff");
            if (cellData.getValue() instanceof Admin) return new SimpleStringProperty("Admin");
            return new SimpleStringProperty("Unknown");
        });
        colDetails.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Customer) {
                Customer c = (Customer) cellData.getValue();
                return new SimpleStringProperty("Accounts: " + c.getAccounts().size() + ", Loans: " + c.getLoans().size() + ", Bills: " + c.getBills().size());
            }
            return new SimpleStringProperty("");
        });
    }

    private void refreshAll() {
        refreshUsers();
        refreshLogs();
    }

    private void refreshUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        users.addAll(bankData.getAllCustomers());
        users.addAll(bankData.getAllStaff());
        users.addAll(bankData.getAllAdmins());
        usersTable.setItems(users);
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
    private void addStaff() {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add Staff");
        usernameDialog.setHeaderText("Enter username:");
        usernameDialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setHeaderText("Enter password:");
            passwordDialog.showAndWait().ifPresent(password -> {
                Staff staff = new Staff(username, password);
                bankData.addStaff(staff);
                refreshUsers();
                showAlert("Staff added.");
            });
        });
    }

    @FXML
    private void addAdmin() {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add Admin");
        usernameDialog.setHeaderText("Enter username:");
        usernameDialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setHeaderText("Enter password:");
            passwordDialog.showAndWait().ifPresent(password -> {
                Admin admin = new Admin(username, password);
                bankData.addAdmin(admin);
                refreshUsers();
                showAlert("Admin added.");
            });
        });
    }

    @FXML
    private void generateReport() {
        String type = reportTypeCombo.getSelectionModel().getSelectedItem();
        reportTable.getColumns().clear();
        reportTable.getItems().clear();

        if (type.equals("Customer Transaction Summary")) {
            TableColumn<ObservableList<String>, String> col1 = new TableColumn<>("Customer");
            TableColumn<ObservableList<String>, String> col2 = new TableColumn<>("Transactions");
            reportTable.getColumns().addAll(col1, col2);
            col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
            col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));

            for (Customer c : bankData.getAllCustomers()) {
                ObservableList<String> row = FXCollections.observableArrayList(c.getUsername(), String.valueOf(c.getTransactions().size()));
                reportTable.getItems().add(row);
            }
        } else if (type.equals("Loan Performance")) {
            TableColumn<ObservableList<String>, String> col1 = new TableColumn<>("Customer");
            TableColumn<ObservableList<String>, String> col2 = new TableColumn<>("Loan ID");
            TableColumn<ObservableList<String>, String> col3 = new TableColumn<>("Amount");
            TableColumn<ObservableList<String>, String> col4 = new TableColumn<>("Status");
            reportTable.getColumns().addAll(col1, col2, col3, col4);
            col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
            col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
            col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
            col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));

            for (Customer c : bankData.getAllCustomers()) {
                for (Loan l : c.getLoans()) {
                    ObservableList<String> row = FXCollections.observableArrayList(
                        c.getUsername(),
                        l.getLoanId(),
                        String.valueOf(l.getAmount()),
                        l.getStatus()
                    );
                    reportTable.getItems().add(row);
                }
            }
        } else if (type.equals("Bill Payment Summary")) {
            TableColumn<ObservableList<String>, String> col1 = new TableColumn<>("Customer");
            TableColumn<ObservableList<String>, String> col2 = new TableColumn<>("Bill ID");
            TableColumn<ObservableList<String>, String> col3 = new TableColumn<>("Type");
            TableColumn<ObservableList<String>, String> col4 = new TableColumn<>("Paid");
            reportTable.getColumns().addAll(col1, col2, col3, col4);
            col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
            col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
            col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
            col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));

            for (Customer c : bankData.getAllCustomers()) {
                for (BillPayment b : c.getBills()) {
                    ObservableList<String> row = FXCollections.observableArrayList(
                        c.getUsername(),
                        b.getBillId(),
                        b.getType(),
                        b.isPaid() ? "Yes" : "No"
                    );
                    reportTable.getItems().add(row);
                }
            }
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Logout?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) usersTable.getScene().getWindow();
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