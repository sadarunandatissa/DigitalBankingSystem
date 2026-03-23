package com.bank.ui;

import com.bank.main.*;
import com.bank.util.BankData;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private BankData bankData = BankData.getInstance();

    public LoginFrame() {
        setTitle("Digital Banking System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username label and field
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> authenticate());
        add(loginBtn, gbc);

        setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Check customer
        if (bankData.authenticateCustomer(username, password)) {
            new CustomerDashboardFrame(bankData.getCustomer(username)).setVisible(true);
            dispose();
            return;
        }

        // Check staff
        if (bankData.authenticateStaff(username, password)) {
            new StaffDashboardFrame(bankData.getStaff(username)).setVisible(true);
            dispose();
            return;
        }

        // Check admin
        if (bankData.authenticateAdmin(username, password)) {
            new AdminDashboardFrame(bankData.getAdmin(username)).setVisible(true);
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}