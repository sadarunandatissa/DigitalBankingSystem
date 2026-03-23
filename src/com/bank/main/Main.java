package com.bank.main;

import com.bank.model.Customer;
import com.bank.model.SavingsAccount;
import com.bank.ui.LoginFrame;
import com.bank.util.BankData;

public class Main {
    public static void main(String[] args) {
        try {
            BankData.getInstance().loadData();
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh.");
        }

        // Add a shutdown hook to save data when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BankData.getInstance().saveData();
                System.out.println("Data saved successfully.");
            } catch (Exception e) {
                System.err.println("Error saving data: " + e.getMessage());
            }
        }));

        // For testing: create a test customer if none exists
if (BankData.getInstance().getCustomer("testuser") == null) {
    try {
        Customer test = new Customer("testuser", "pass123");
        SavingsAccount testAcc = new SavingsAccount(1000);
        test.addAccount(testAcc);
        BankData.getInstance().addCustomer(test);
        System.out.println("Test customer created.");
    } catch (Exception e) {
        System.out.println("Error creating test customer: " + e.getMessage());
    }
}
        // Launch GUI
        new LoginFrame().setVisible(true);
    }
}