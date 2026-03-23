package com.bank.ui;

import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 50, 150, 30);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 100, 150, 30);
        add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 150, 100, 30);
        add(loginBtn);
    }
}