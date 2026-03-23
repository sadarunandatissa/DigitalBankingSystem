package com.bank.main;

import com.bank.util.BankData;

public class Main {
    public static void main(String[] args) {
        // Load data once
        try {
            BankData.getInstance().loadData();
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh.");
        }

        // Shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BankData.getInstance().saveData();
                System.out.println("Data saved successfully.");
            } catch (Exception e) {
                System.err.println("Error saving data: " + e.getMessage());
            }
        }));

        // Launch the new login manager (console-based for now)
        LoginManager.main(args);
    }
}