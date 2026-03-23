package com.bank.main;

import com.bank.model.*;
import com.bank.service.BankService;
import com.bank.util.BankData;
import com.bank.util.AuditLogger;
import java.util.Scanner;

public class BankConsole {
    private static Scanner scanner = new Scanner(System.in);
    private static Customer currentCustomer = null;

    public static void main(String[] args) {
        // Load existing data
        try {
            BankData.getInstance().loadData();
            System.out.println("Data loaded.");
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh.");
        }

        // Create a test customer if none exists
        if (BankData.getInstance().getCustomer("test") == null) {
            Customer test = new Customer("test", "1234");
            SavingsAccount savings = new SavingsAccount(1000);
            CheckingAccount checking = new CheckingAccount(200);
            test.addAccount(savings);
            test.addAccount(checking);
            BankData.getInstance().addCustomer(test);
            System.out.println("Test customer created (username: test, password: 1234)");
        }

        // Login loop
        while (true) {
            System.out.print("\nUsername: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            Customer cust = BankData.getInstance().getCustomer(user);
            if (cust != null && cust.getPassword().equals(pass)) {
                currentCustomer = cust;
                System.out.println("Login successful. Welcome " + user + "!");
                showCustomerMenu();
            } else {
                AuditLogger.log("Failed login attempt for user: " + user);
                System.out.println("Invalid credentials. Try again.");
            }
        }
    }

    private static void showCustomerMenu() {
        while (true) {
            System.out.println("\n=== Customer Dashboard ===");
            System.out.println("1. View Accounts");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewAccounts(); break;
                case "2": deposit(); break;
                case "3": withdraw(); break;
                case "4": transfer(); break;
                case "5": showTransactionHistory(); break;
                case "6": System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAccounts() {
        System.out.println("\nYour Accounts:");
        for (Account acc : currentCustomer.getAccounts()) {
            System.out.printf("%s - Balance: $%.2f%n", acc.getAccountNumber(), acc.getBalance());
        }
    }

    private static void deposit() {
        Account acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            acc.deposit(amount);
            currentCustomer.addTransaction(new Transaction("DEPOSIT", amount, acc));
            System.out.println("Deposit successful. New balance: $" + acc.getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AuditLogger.log("Deposit failed for account " + acc.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private static void withdraw() {
        Account acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            acc.withdraw(amount);
            currentCustomer.addTransaction(new Transaction("WITHDRAWAL", amount, acc));
            System.out.println("Withdrawal successful. New balance: $" + acc.getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AuditLogger.log("Withdrawal failed for account " + acc.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private static void transfer() {
        System.out.println("\nSource Account:");
        Account source = selectAccount();
        if (source == null) return;
        System.out.println("Destination Account:");
        Account dest = selectAccount();
        if (dest == null) return;
        System.out.print("Amount to transfer: ");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            BankService.transfer(source, dest, amount);
            currentCustomer.addTransaction(new Transaction("TRANSFER_OUT", amount, source));
            currentCustomer.addTransaction(new Transaction("TRANSFER_IN", amount, dest));
            System.out.println("Transfer successful.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
            AuditLogger.log("Transfer failed from " + source.getAccountNumber() + " to " + dest.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private static void showTransactionHistory() {
        System.out.println("\nTransaction History:");
        for (Transaction t : currentCustomer.getTransactions()) {
            System.out.println(t);
        }
    }

    private static Account selectAccount() {
        if (currentCustomer.getAccounts().isEmpty()) {
            System.out.println("You have no accounts.");
            return null;
        }
        System.out.println("Select account:");
        for (int i = 0; i < currentCustomer.getAccounts().size(); i++) {
            Account acc = currentCustomer.getAccounts().get(i);
            System.out.printf("%d. %s (Balance: $%.2f)%n", i+1, acc.getAccountNumber(), acc.getBalance());
        }
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx >= 0 && idx < currentCustomer.getAccounts().size()) {
            return currentCustomer.getAccounts().get(idx);
        }
        System.out.println("Invalid selection.");
        return null;
    }
}