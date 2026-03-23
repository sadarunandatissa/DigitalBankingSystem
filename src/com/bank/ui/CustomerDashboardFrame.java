package com.bank.ui;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class CustomerDashboardFrame extends JFrame {
    private Customer customer;
    private JTabbedPane tabbedPane;
    private DefaultTableModel accountTableModel;
    private DefaultTableModel transactionTableModel;
    private DefaultTableModel loanTableModel;
    private DefaultTableModel billTableModel;
    private JTextArea notificationArea;

    public CustomerDashboardFrame(Customer customer) {
        this.customer = customer;
        setTitle("Customer Dashboard - " + customer.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Logout menu ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Accounts", createAccountsPanel());
        tabbedPane.addTab("Transactions", createTransactionsPanel());
        tabbedPane.addTab("Loans", createLoansPanel());
        tabbedPane.addTab("Bills", createBillsPanel());
        tabbedPane.addTab("Notifications", createNotificationsPanel());

        add(tabbedPane);
        refreshAll();
    }

    // ---------- Panel creation ----------
    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        accountTableModel = new DefaultTableModel(new String[]{"Account No.", "Type", "Balance"}, 0);
        JTable table = new JTable(accountTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(transferBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        transferBtn.addActionListener(e -> transfer());

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        transactionTableModel = new DefaultTableModel(new String[]{"ID", "Type", "Amount", "Date", "Account"}, 0);
        JTable table = new JTable(transactionTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        loanTableModel = new DefaultTableModel(new String[]{"Loan ID", "Amount", "Rate", "Term", "Monthly Payment", "Status"}, 0);
        JTable table = new JTable(loanTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton applyBtn = new JButton("Apply for Loan");
        applyBtn.addActionListener(e -> applyForLoan());
        panel.add(applyBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        billTableModel = new DefaultTableModel(new String[]{"Bill ID", "Type", "Amount", "Due Date", "Paid"}, 0);
        JTable table = new JTable(billTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton payBtn = new JButton("Pay Selected Bill");
        payBtn.addActionListener(e -> payBill(table.getSelectedRow()));
        panel.add(payBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        notificationArea = new JTextArea(10, 40);
        notificationArea.setEditable(false);
        panel.add(new JScrollPane(notificationArea), BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh Notifications");
        refreshBtn.addActionListener(e -> refreshNotifications());
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Refresh methods ----------
    private void refreshAll() {
        refreshAccounts();
        refreshTransactions();
        refreshLoans();
        refreshBills();
        refreshNotifications();
    }

    private void refreshAccounts() {
        accountTableModel.setRowCount(0);
        for (Account acc : customer.getAccounts()) {
            String type = acc.getClass().getSimpleName();
            accountTableModel.addRow(new Object[]{acc.getAccountNumber(), type, acc.getBalance()});
        }
    }

    private void refreshTransactions() {
        transactionTableModel.setRowCount(0);
        for (Transaction t : customer.getTransactions()) {
            transactionTableModel.addRow(new Object[]{t.getTransactionId(), t.getType(), t.getAmount(), t.getTimestamp(), t.getAccountNumber()});
        }
    }

    private void refreshLoans() {
        loanTableModel.setRowCount(0);
        for (Loan l : customer.getLoans()) {
            loanTableModel.addRow(new Object[]{l.getLoanId(), l.getAmount(), l.getInterestRate(), l.getTermMonths(), l.getMonthlyPayment(), l.getStatus()});
        }
    }

    private void refreshBills() {
        billTableModel.setRowCount(0);
        for (BillPayment b : customer.getBills()) {
            billTableModel.addRow(new Object[]{b.getBillId(), b.getType(), b.getAmount(), b.getDueDate(), b.isPaid() ? "Yes" : "No"});
        }
    }

    private void refreshNotifications() {
        StringBuilder sb = new StringBuilder();
        // Low balance alerts
        for (Account acc : customer.getAccounts()) {
            if (acc.getBalance() < 100) {
                sb.append("Low balance in account ").append(acc.getAccountNumber()).append(": $").append(acc.getBalance()).append("\n");
            }
        }
        // Loan reminders
        for (Loan loan : customer.getLoans()) {
            if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("ACTIVE")) {
                if (loan.getNextDueDate().isBefore(LocalDate.now().plusDays(3))) {
                    sb.append("Loan ").append(loan.getLoanId()).append(" payment due on ").append(loan.getNextDueDate()).append("\n");
                }
            }
        }
        // Bill reminders
        for (BillPayment bill : customer.getBills()) {
            if (!bill.isPaid() && bill.getDueDate().isBefore(LocalDate.now().plusDays(3))) {
                sb.append("Bill ").append(bill.getBillId()).append(" (").append(bill.getType()).append(") due on ").append(bill.getDueDate()).append("\n");
            }
        }
        if (sb.length() == 0) sb.append("No new notifications.");
        notificationArea.setText(sb.toString());
    }

    // ---------- Operations ----------
    private void deposit() {
        Object[] accounts = customer.getAccounts().toArray();
        int selected = JOptionPane.showOptionDialog(this, "Select account:", "Deposit", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, accounts, null);
        if (selected < 0) return;
        Account acc = customer.getAccounts().get(selected);
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amountStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            acc.deposit(amount);
            customer.addTransaction(new Transaction("DEPOSIT", amount, acc));
            JOptionPane.showMessageDialog(this, "Deposit successful. New balance: $" + acc.getBalance());
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Deposit Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void withdraw() {
        Object[] accounts = customer.getAccounts().toArray();
        int selected = JOptionPane.showOptionDialog(this, "Select account:", "Withdraw", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, accounts, null);
        if (selected < 0) return;
        Account acc = customer.getAccounts().get(selected);
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amountStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            acc.withdraw(amount);
            customer.addTransaction(new Transaction("WITHDRAWAL", amount, acc));
            JOptionPane.showMessageDialog(this, "Withdrawal successful. New balance: $" + acc.getBalance());
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void transfer() {
        Object[] accounts = customer.getAccounts().toArray();
        int sourceIdx = JOptionPane.showOptionDialog(this, "Select source account:", "Transfer", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, accounts, null);
        if (sourceIdx < 0) return;
        int destIdx = JOptionPane.showOptionDialog(this, "Select destination account:", "Transfer", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, accounts, null);
        if (destIdx < 0) return;
        if (sourceIdx == destIdx) {
            JOptionPane.showMessageDialog(this, "Cannot transfer to same account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Account source = customer.getAccounts().get(sourceIdx);
        Account dest = customer.getAccounts().get(destIdx);
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to transfer:");
        if (amountStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            BankService.transfer(source, dest, amount);
            customer.addTransaction(new Transaction("TRANSFER_OUT", amount, source));
            customer.addTransaction(new Transaction("TRANSFER_IN", amount, dest));
            JOptionPane.showMessageDialog(this, "Transfer successful.");
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Transfer failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyForLoan() {
        String amountStr = JOptionPane.showInputDialog(this, "Loan amount:");
        if (amountStr == null) return;
        String rateStr = JOptionPane.showInputDialog(this, "Interest rate (%):");
        if (rateStr == null) return;
        String termStr = JOptionPane.showInputDialog(this, "Term (months):");
        if (termStr == null) return;
        try {
            double amount = Double.parseDouble(amountStr);
            double rate = Double.parseDouble(rateStr);
            int months = Integer.parseInt(termStr);
            LoanService.applyForLoan(customer, amount, rate, months);
            JOptionPane.showMessageDialog(this, "Loan application submitted.");
            refreshLoans();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void payBill(int row) {
        if (row < 0 || row >= customer.getBills().size()) {
            JOptionPane.showMessageDialog(this, "Select a bill first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BillPayment bill = customer.getBills().get(row);
        if (bill.isPaid()) {
            JOptionPane.showMessageDialog(this, "Bill already paid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object[] accounts = customer.getAccounts().toArray();
        int accIdx = JOptionPane.showOptionDialog(this, "Select account to pay from:", "Pay Bill", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, accounts, null);
        if (accIdx < 0) return;
        Account acc = customer.getAccounts().get(accIdx);
        try {
            BillService.payBill(customer, bill, acc);
            JOptionPane.showMessageDialog(this, "Bill paid successfully.");
            refreshAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Payment failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Logout ----------
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}