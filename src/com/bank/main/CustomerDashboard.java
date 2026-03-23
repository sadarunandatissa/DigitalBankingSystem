package com.bank.main;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.*;
import java.time.LocalDate;
import java.util.Scanner;

public class CustomerDashboard {
    private Customer customer;
    private Scanner scanner = new Scanner(System.in);
    private BankData bankData = BankData.getInstance();

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Customer Dashboard ===");
            System.out.println("1. View Accounts");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.println("7. Apply for Loan");
            System.out.println("8. View Loans");
            System.out.println("9. Pay Bills");
            System.out.println("10. View Bills");
            System.out.println("11. Check Notifications");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewAccounts(); break;
                case "2": deposit(); break;
                case "3": withdraw(); break;
                case "4": transfer(); break;
                case "5": showTransactionHistory(); break;
                case "6": System.out.println("Logging out..."); return;
                case "7": applyForLoan(); break;
                case "8": viewLoans(); break;
                case "9": payBills(); break;
                case "10": viewBills(); break;
                case "11": checkNotifications(); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------- Existing methods ----------
    private void viewAccounts() {
        System.out.println("\nYour Accounts:");
        for (Account acc : customer.getAccounts()) {
            System.out.printf("%s - Balance: $%.2f%n", acc.getAccountNumber(), acc.getBalance());
        }
    }

    private void deposit() {
        Account acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            acc.deposit(amount);
            customer.addTransaction(new Transaction("DEPOSIT", amount, acc));
            System.out.println("Deposit successful. New balance: $" + acc.getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AuditLogger.log("Deposit failed for account " + acc.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private void withdraw() {
        Account acc = selectAccount();
        if (acc == null) return;
        System.out.print("Amount to withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            acc.withdraw(amount);
            customer.addTransaction(new Transaction("WITHDRAWAL", amount, acc));
            System.out.println("Withdrawal successful. New balance: $" + acc.getBalance());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            AuditLogger.log("Withdrawal failed for account " + acc.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private void transfer() {
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
            customer.addTransaction(new Transaction("TRANSFER_OUT", amount, source));
            customer.addTransaction(new Transaction("TRANSFER_IN", amount, dest));
            System.out.println("Transfer successful.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
            AuditLogger.log("Transfer failed from " + source.getAccountNumber() + " to " + dest.getAccountNumber() + ": " + e.getMessage());
        }
    }

    private void showTransactionHistory() {
        System.out.println("\nTransaction History:");
        for (Transaction t : customer.getTransactions()) {
            System.out.println(t);
        }
    }

    private Account selectAccount() {
        if (customer.getAccounts().isEmpty()) {
            System.out.println("You have no accounts.");
            return null;
        }
        System.out.println("Select account:");
        for (int i = 0; i < customer.getAccounts().size(); i++) {
            Account acc = customer.getAccounts().get(i);
            System.out.printf("%d. %s (Balance: $%.2f)%n", i+1, acc.getAccountNumber(), acc.getBalance());
        }
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx >= 0 && idx < customer.getAccounts().size()) {
            return customer.getAccounts().get(idx);
        }
        System.out.println("Invalid selection.");
        return null;
    }

    // ---------- New loan/bill/notification methods ----------
    private void applyForLoan() {
        System.out.print("Enter loan amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter interest rate (%): ");
        double rate = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter term (months): ");
        int months = Integer.parseInt(scanner.nextLine());
        LoanService.applyForLoan(customer, amount, rate, months);
        System.out.println("Loan application submitted. Staff will review it.");
    }

    private void viewLoans() {
        System.out.println("\nYour Loans:");
        if (customer.getLoans().isEmpty()) {
            System.out.println("No loans found.");
        }
        for (Loan loan : customer.getLoans()) {
            System.out.println(loan);
            if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("ACTIVE")) {
                System.out.println("  Monthly payment: $" + loan.getMonthlyPayment());
                System.out.println("  Next due date: " + loan.getNextDueDate());
            }
        }
    }

    private void payBills() {
        // Filter unpaid bills
        java.util.List<BillPayment> unpaid = new java.util.ArrayList<>();
        for (BillPayment b : customer.getBills()) {
            if (!b.isPaid()) unpaid.add(b);
        }
        if (unpaid.isEmpty()) {
            System.out.println("No unpaid bills.");
            return;
        }
        System.out.println("Select a bill to pay:");
        for (int i = 0; i < unpaid.size(); i++) {
            BillPayment b = unpaid.get(i);
            System.out.printf("%d. %s - $%.2f (due %s)%n", i+1, b.getType(), b.getAmount(), b.getDueDate());
        }
        System.out.print("Choice: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= unpaid.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        BillPayment selected = unpaid.get(idx);
        Account acc = selectAccount();
        if (acc == null) return;
        try {
            BillService.payBill(customer, selected, acc);
            System.out.println("Bill paid successfully.");
        } catch (Exception e) {
            System.out.println("Payment failed: " + e.getMessage());
        }
    }

    private void viewBills() {
        System.out.println("\nYour Bills:");
        if (customer.getBills().isEmpty()) {
            System.out.println("No bills found.");
        }
        for (BillPayment b : customer.getBills()) {
            System.out.println(b);
        }
    }

    private void checkNotifications() {
        NotificationService.checkAndNotify(customer);
    }
}