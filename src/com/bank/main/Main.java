package com.bank.main;

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

        // Launch GUI
        new LoginFrame().setVisible(true);
    }
}