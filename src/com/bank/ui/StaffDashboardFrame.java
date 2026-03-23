package com.bank.ui;

import com.bank.model.*;
import com.bank.service.LoanService;
import com.bank.util.BankData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

public class StaffDashboardFrame extends JFrame {
    private Staff staff;
    private BankData bankData = BankData.getInstance();
    private JTabbedPane tabbedPane;

    // Tables
    private DefaultTableModel customerTableModel;
    private DefaultTableModel pendingLoanTableModel;
    private JTextArea logArea;

    public StaffDashboardFrame(Staff staff) {
        this.staff = staff;
        setTitle("Staff Dashboard - " + staff.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Customers", createCustomersPanel());
        tabbedPane.addTab("Pending Loans", createPendingLoansPanel());
        tabbedPane.addTab("Activity Logs", createLogsPanel());

        add(tabbedPane);
        refreshAll();
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        customerTableModel = new DefaultTableModel(new String[]{"Username", "Accounts", "Loans", "Bills"}, 0);
        JTable table = new JTable(customerTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshCustomers());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPendingLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        pendingLoanTableModel = new DefaultTableModel(new String[]{"Loan ID", "Customer", "Amount", "Rate", "Term", "Status"}, 0);
        JTable table = new JTable(pendingLoanTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton approveBtn = new JButton("Approve Selected");
        JButton rejectBtn = new JButton("Reject Selected");
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> approveLoan(table.getSelectedRow()));
        rejectBtn.addActionListener(e -> rejectLoan(table.getSelectedRow()));

        return panel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        logArea = new JTextArea(20, 60);
        logArea.setEditable(false);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh Logs");
        refreshBtn.addActionListener(e -> refreshLogs());
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAll() {
        refreshCustomers();
        refreshPendingLoans();
        refreshLogs();
    }

    private void refreshCustomers() {
        customerTableModel.setRowCount(0);
        Collection<Customer> customers = bankData.getAllCustomers();
        for (Customer c : customers) {
            customerTableModel.addRow(new Object[]{
                c.getUsername(),
                c.getAccounts().size(),
                c.getLoans().size(),
                c.getBills().size()
            });
        }
    }

    private void refreshPendingLoans() {
        pendingLoanTableModel.setRowCount(0);
        Collection<Customer> customers = bankData.getAllCustomers();
        for (Customer c : customers) {
            for (Loan loan : c.getLoans()) {
                if (loan.getStatus().equals("PENDING")) {
                    pendingLoanTableModel.addRow(new Object[]{
                        loan.getLoanId(),
                        c.getUsername(),
                        loan.getAmount(),
                        loan.getInterestRate(),
                        loan.getTermMonths(),
                        loan.getStatus()
                    });
                }
            }
        }
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
            logArea.setText("Could not read log file: " + e.getMessage());
        }
    }

    private void approveLoan(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a loan to approve.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String loanId = (String) pendingLoanTableModel.getValueAt(row, 0);
        // Find the loan object
        Collection<Customer> customers = bankData.getAllCustomers();
        for (Customer c : customers) {
            for (Loan loan : c.getLoans()) {
                if (loan.getLoanId().equals(loanId) && loan.getStatus().equals("PENDING")) {
                    LoanService.approveLoan(loan);
                    JOptionPane.showMessageDialog(this, "Loan " + loanId + " approved.");
                    refreshAll();
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Loan not found or already processed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void rejectLoan(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a loan to reject.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String loanId = (String) pendingLoanTableModel.getValueAt(row, 0);
        Collection<Customer> customers = bankData.getAllCustomers();
        for (Customer c : customers) {
            for (Loan loan : c.getLoans()) {
                if (loan.getLoanId().equals(loanId) && loan.getStatus().equals("PENDING")) {
                    LoanService.rejectLoan(loan);
                    JOptionPane.showMessageDialog(this, "Loan " + loanId + " rejected.");
                    refreshAll();
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Loan not found or already processed.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}