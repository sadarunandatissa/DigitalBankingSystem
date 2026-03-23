package com.bank.service;

import com.bank.model.*;
import com.bank.util.AuditLogger;
import java.time.LocalDate;
import java.util.List;

public class NotificationService {

    public static void checkAndNotify(Customer customer) {
        // 1. Low balance alerts
        for (Account acc : customer.getAccounts()) {
            if (acc.getBalance() < 100) {
                System.out.println("[ALERT] Low balance in account " + acc.getAccountNumber() + ": $" + acc.getBalance());
                AuditLogger.log("Low balance alert for " + customer.getUsername() + ", account " + acc.getAccountNumber());
            }
        }

        // 2. Loan due reminders
        for (Loan loan : customer.getLoans()) {
            if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("ACTIVE")) {
                LocalDate due = loan.getNextDueDate();
                if (due.isBefore(LocalDate.now().plusDays(3)) && !due.isBefore(LocalDate.now())) {
                    System.out.println("[REMINDER] Loan " + loan.getLoanId() + " payment due on " + due);
                }
            }
        }

        // 3. Bill due reminders
        for (BillPayment bill : customer.getBills()) {
            if (!bill.isPaid() && bill.getDueDate().isBefore(LocalDate.now().plusDays(3))) {
                System.out.println("[REMINDER] Bill " + bill.getBillId() + " (" + bill.getType() + ") due on " + bill.getDueDate());
            }
        }
    }
}