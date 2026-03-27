package com.bank.service;

import com.bank.model.*;
import com.bank.util.AuditLogger;

public class BillService {
    public static void payBill(Customer customer, BillPayment bill, Account account) throws Exception {
        if (bill.isPaid()) {
            throw new Exception("Bill already paid.");
        }
        if (account.getBalance() < bill.getAmount()) {
            throw new Exception("Insufficient funds to pay bill.");
        }
        account.withdraw(bill.getAmount());
        bill.setPaid(true);
        customer.addTransaction(new Transaction("BILL_PAYMENT", bill.getAmount(), account));
        AuditLogger.log("Bill " + bill.getBillId() + " paid by " + customer.getUsername() + " from account " + account.getAccountNumber());
    }

    public static void addBill(Customer customer, String type, double amount, java.time.LocalDate dueDate) {
        BillPayment bill = new BillPayment(type, amount, dueDate);
        customer.addBill(bill);
    }
}