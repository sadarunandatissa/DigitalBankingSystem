package com.bank.ui;

import com.bank.model.*;
import com.bank.util.BankData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

public class AdminDashboardFrame extends JFrame {
    private Admin admin;
    private BankData bankData = BankData.getInstance();
    private JTabbedPane tabbedPane;

    // Tables
    private DefaultTableModel userTableModel;
    private DefaultTableModel reportTableModel;
    private JTextArea logArea;

    public AdminDashboardFrame(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("System Logs", createLogsPanel());

        add(tabbedPane);
        refreshAll();
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for users
        userTableModel = new DefaultTableModel(new String[]{"Username", "Role", "Accounts (if customer)", "Loans/Bills"}, 0);
        JTable table = new JTable(userTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons for adding users
        JPanel buttonPanel = new JPanel();
        JButton addStaffBtn = new JButton("Add Staff");
        JButton addAdminBtn = new JButton("Add Admin");
        JButton refreshBtn = new JButton("Refresh");
        buttonPanel.add(addStaffBtn);
        buttonPanel.add(addAdminBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addStaffBtn.addActionListener(e -> addUser("staff"));
        addAdminBtn.addActionListener(e -> addUser("admin"));
        refreshBtn.addActionListener(e -> refreshUsers());

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Dropdown for report type
        JPanel topPanel = new JPanel();
        JLabel reportLabel = new JLabel("Report Type:");
        JComboBox<String> reportType = new JComboBox<>(new String[]{"Customer Transaction Summary", "Loan Performance", "Bill Payment Summary"});
        JButton generateBtn = new JButton("Generate");
        topPanel.add(reportLabel);
        topPanel.add(reportType);
        topPanel.add(generateBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table to display report data
        reportTableModel = new DefaultTableModel();
        JTable table = new JTable(reportTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        generateBtn.addActionListener(e -> generateReport((String) reportType.getSelectedItem()));

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
        refreshUsers();
        refreshLogs();
    }

    private void refreshUsers() {
        userTableModel.setRowCount(0);
        // Customers
        for (Customer c : bankData.getAllCustomers()) {
            userTableModel.addRow(new Object[]{c.getUsername(), "Customer", c.getAccounts().size(), c.getLoans().size() + "/" + c.getBills().size()});
        }
        // Staff - we need a method to get all staff; we'll add a temporary getter in BankData
        // For now, just add a placeholder message.
        userTableModel.addRow(new Object[]{"(Staff list requires getAllStaff())", "Staff", "-", "-"});
        // Admin
        userTableModel.addRow(new Object[]{"(Admin list requires getAllAdmins())", "Admin", "-", "-"});
    }

    private void addUser(String role) {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username == null || username.trim().isEmpty()) return;
        String password = JOptionPane.showInputDialog(this, "Enter password:");
        if (password == null) return;
        if (role.equals("staff")) {
            Staff newStaff = new Staff(username, password);
            bankData.addStaff(newStaff);
            JOptionPane.showMessageDialog(this, "Staff added.");
        } else if (role.equals("admin")) {
            Admin newAdmin = new Admin(username, password);
            bankData.addAdmin(newAdmin);
            JOptionPane.showMessageDialog(this, "Admin added.");
        }
        try {
            bankData.saveData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        refreshUsers();
    }

    private void generateReport(String reportType) {
        reportTableModel.setRowCount(0);
        switch (reportType) {
            case "Customer Transaction Summary":
                reportTableModel.setColumnIdentifiers(new String[]{"Customer", "Total Transactions"});
                for (Customer c : bankData.getAllCustomers()) {
                    reportTableModel.addRow(new Object[]{c.getUsername(), c.getTransactions().size()});
                }
                break;
            case "Loan Performance":
                reportTableModel.setColumnIdentifiers(new String[]{"Customer", "Loan ID", "Amount", "Status"});
                for (Customer c : bankData.getAllCustomers()) {
                    for (Loan l : c.getLoans()) {
                        reportTableModel.addRow(new Object[]{c.getUsername(), l.getLoanId(), l.getAmount(), l.getStatus()});
                    }
                }
                break;
            case "Bill Payment Summary":
                reportTableModel.setColumnIdentifiers(new String[]{"Customer", "Bill ID", "Type", "Paid"});
                for (Customer c : bankData.getAllCustomers()) {
                    for (BillPayment b : c.getBills()) {
                        reportTableModel.addRow(new Object[]{c.getUsername(), b.getBillId(), b.getType(), b.isPaid() ? "Yes" : "No"});
                    }
                }
                break;
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
}