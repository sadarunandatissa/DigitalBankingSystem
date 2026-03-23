package com.bank.service;

import com.bank.model.*;
import com.bank.util.AuditLogger;
import java.time.LocalDate;

public class BillService {

    // Customer pays a bill from an account
    public static void payBill(Customer customer, BillPayment bill, Account account) throws Exception {
        if (bill.isPaid()) {
            throw new Exception("Bill already paid.");
        }
        if (account.getBalance() < bill.getAmount()) {
            throw new Exception("Insufficient funds to pay bill.");
        }
        // Withdraw from account
        account.withdraw(bill.getAmount());
        // Mark bill as paid
        bill.setPaid(true);
        // Record transaction
        customer.addTransaction(new Transaction("BILL_PAYMENT", bill.getAmount(), account));
        AuditLogger.log("Bill " + bill.getBillId() + " paid by " + customer.getUsername() + " from account " + account.getAccountNumber());
    }

    // Add a new bill (for testing)
    public static void addBill(Customer customer, String type, double amount, LocalDate dueDate) {
        BillPayment bill = new BillPayment(type, amount, dueDate);
        customer.addBill(bill);
    }
}