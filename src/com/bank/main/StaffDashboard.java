package com.bank.main;

import com.bank.model.*;
import com.bank.service.LoanService;
import com.bank.util.*;
import java.util.Scanner;

public class StaffDashboard {
    private Staff staff;
    private Scanner scanner = new Scanner(System.in);
    private BankData bankData = BankData.getInstance();

    public StaffDashboard(Staff staff) {
        this.staff = staff;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Staff Dashboard ===");
            System.out.println("1. View All Customers");
            System.out.println("2. View Suspicious Activity (Logs)");
            System.out.println("3. Approve Loans (Coming Soon)");
            System.out.println("4. Manage Loan Applications");
            System.out.println("5. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewAllCustomers(); break;
                case "2": viewSuspiciousActivity(); break;
                case "3": System.out.println("Loan approval feature is now under option 4."); break;
                case "4": manageLoans(); break;
                case "5": System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllCustomers() {
        java.util.Collection<Customer> allCustomers = bankData.getAllCustomers();
        if (allCustomers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        System.out.println("\nAll Customers:");
        for (Customer c : allCustomers) {
            System.out.println("Username: " + c.getUsername());
            System.out.println("  Accounts: " + c.getAccounts().size());
            System.out.println("  Loans: " + c.getLoans().size());
            System.out.println("  Bills: " + c.getBills().size());
        }
    }

    private void viewSuspiciousActivity() {
        try {
            java.nio.file.Path logPath = java.nio.file.Paths.get("logs/invalid_ops.log");
            if (java.nio.file.Files.exists(logPath)) {
                System.out.println("\n--- Suspicious Activity Log ---");
                java.nio.file.Files.lines(logPath).forEach(System.out::println);
            } else {
                System.out.println("No suspicious activity logged yet.");
            }
        } catch (Exception e) {
            System.out.println("Could not read log file.");
        }
    }

    private void manageLoans() {
        java.util.Collection<Customer> allCustomers = bankData.getAllCustomers();
        boolean found = false;
        System.out.println("\nPending Loan Applications:");
        for (Customer c : allCustomers) {
            for (Loan loan : c.getLoans()) {
                if (loan.getStatus().equals("PENDING")) {
                    found = true;
                    System.out.printf("Loan %s for customer %s: $%.2f @ %.2f%% for %d months%n",
                            loan.getLoanId(), c.getUsername(), loan.getAmount(), loan.getInterestRate(), loan.getTermMonths());
                    System.out.print("Approve (y/n)? ");
                    String ans = scanner.nextLine();
                    if (ans.equalsIgnoreCase("y")) {
                        LoanService.approveLoan(loan);
                        System.out.println("Loan approved.");
                    } else if (ans.equalsIgnoreCase("n")) {
                        LoanService.rejectLoan(loan);
                        System.out.println("Loan rejected.");
                    } else {
                        System.out.println("Invalid input, skipping.");
                    }
                }
            }
        }
        if (!found) {
            System.out.println("No pending loan applications.");
        }
    }
}