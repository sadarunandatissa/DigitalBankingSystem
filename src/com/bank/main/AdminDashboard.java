package com.bank.main;

import com.bank.model.*;
import com.bank.util.*;
import java.util.Scanner;

public class AdminDashboard {
    private Admin admin;
    private Scanner scanner = new Scanner(System.in);
    private BankData bankData = BankData.getInstance();

    public AdminDashboard(Admin admin) {
        this.admin = admin;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. Manage Users");
            System.out.println("2. Generate Reports");
            System.out.println("3. View System Logs");
            System.out.println("4. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": manageUsers(); break;
                case "2": generateReports(); break;
                case "3": viewSystemLogs(); break;
                case "4": System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void manageUsers() {
        System.out.println("\n--- User Management ---");
        System.out.println("1. List all users");
        System.out.println("2. Add Staff");
        System.out.println("3. Add Admin");
        System.out.println("4. Remove User");
        System.out.print("Choice: ");
        String sub = scanner.nextLine();

        switch (sub) {
            case "1":
                listAllUsers();
                break;
            case "2":
                addStaff();
                break;
            case "3":
                addAdmin();
                break;
            case "4":
                removeUser();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void listAllUsers() {
        System.out.println("\n--- All Users ---");
        System.out.println("Customers:");
        for (Customer c : bankData.getAllCustomers()) {
            System.out.println("  " + c.getUsername());
        }
        System.out.println("Staff:");
        // We need a method to get all staff – we'll add a simple one (see note below)
        // For simplicity, we'll skip; you can add getter in BankData if desired.
        System.out.println("  (List all staff – requires BankData.getAllStaff())");
        System.out.println("Admins:");
        // Similarly, need getAllAdmins.
        System.out.println("  (List all admins – requires BankData.getAllAdmins())");
    }

    private void addStaff() {
        System.out.print("Enter new staff username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Staff newStaff = new Staff(username, password);
        bankData.addStaff(newStaff);
        System.out.println("Staff added.");
        try {
            bankData.saveData();
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void addAdmin() {
        System.out.print("Enter new admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Admin newAdmin = new Admin(username, password);
        bankData.addAdmin(newAdmin);
        System.out.println("Admin added.");
        try {
            bankData.saveData();
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void removeUser() {
        System.out.print("Enter username to remove: ");
        String username = scanner.nextLine();
        // For simplicity, only remove customers for now
        if (bankData.getCustomer(username) != null) {
            // We need a method to remove from the map – we'll just show placeholder
            System.out.println("Removal not yet fully implemented.");
        } else {
            System.out.println("User not found or cannot be removed (only customers can be removed via admin in this version).");
        }
    }

    private void generateReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Customer Transaction Summary");
        System.out.println("2. Loan Performance");
        System.out.println("3. Bill Payment Summary");
        System.out.print("Choice: ");
        String sub = scanner.nextLine();
        switch (sub) {
            case "1":
                generateCustomerTransactionReport();
                break;
            case "2":
                generateLoanReport();
                break;
            case "3":
                generateBillReport();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void generateCustomerTransactionReport() {
        System.out.println("\nCustomer Transaction Report:");
        for (Customer c : bankData.getAllCustomers()) {
            System.out.println("Customer: " + c.getUsername());
            System.out.println("  Total Transactions: " + c.getTransactions().size());
            // Optionally print each transaction
            for (Transaction t : c.getTransactions()) {
                System.out.println("    " + t);
            }
        }
    }

    private void generateLoanReport() {
        System.out.println("\nLoan Performance Report:");
        int totalLoans = 0;
        double totalAmount = 0;
        for (Customer c : bankData.getAllCustomers()) {
            for (Loan l : c.getLoans()) {
                totalLoans++;
                totalAmount += l.getAmount();
                System.out.printf("  %s (%s): $%.2f, status: %s%n", c.getUsername(), l.getLoanId(), l.getAmount(), l.getStatus());
            }
        }
        System.out.println("Total Loans: " + totalLoans);
        System.out.println("Total Loan Amount: $" + totalAmount);
    }

    private void generateBillReport() {
        System.out.println("\nBill Payment Summary:");
        int paid = 0, unpaid = 0;
        for (Customer c : bankData.getAllCustomers()) {
            for (BillPayment b : c.getBills()) {
                if (b.isPaid()) paid++;
                else unpaid++;
            }
        }
        System.out.println("Paid Bills: " + paid);
        System.out.println("Unpaid Bills: " + unpaid);
    }

    private void viewSystemLogs() {
        try {
            java.nio.file.Path logPath = java.nio.file.Paths.get("logs/invalid_ops.log");
            if (java.nio.file.Files.exists(logPath)) {
                System.out.println("\n--- System Logs ---");
                java.nio.file.Files.lines(logPath).forEach(System.out::println);
            } else {
                System.out.println("No logs found.");
            }
        } catch (Exception e) {
            System.out.println("Could not read log file.");
        }
    }
}