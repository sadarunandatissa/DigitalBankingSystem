package com.bank.main;

import com.bank.ui.LoginFrame;
import com.bank.util.BankData;

public class Main {
    public static void main(String[] args) {
        try {
            BankData.getInstance().loadData();
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh.");
        }
        // Launch GUI
        new LoginFrame().setVisible(true);
    }
}