package com.bank.service;

import com.bank.model.*;
import java.time.LocalDate;

public class NotificationService {
    public static String getNotifications(Customer customer) {
        StringBuilder sb = new StringBuilder();
        for (Account acc : customer.getAccounts()) {
            if (acc.getBalance() < 100) {
                sb.append("Low balance in account ").append(acc.getAccountNumber())
                  .append(": $").append(acc.getBalance()).append("\n");
            }
        }
        for (Loan loan : customer.getLoans()) {
            if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("ACTIVE")) {
                if (loan.getNextDueDate().isBefore(LocalDate.now().plusDays(3))) {
                    sb.append("Loan ").append(loan.getLoanId())
                      .append(" payment due on ").append(loan.getNextDueDate()).append("\n");
                }
            }
        }
        for (BillPayment bill : customer.getBills()) {
            if (!bill.isPaid() && bill.getDueDate().isBefore(LocalDate.now().plusDays(3))) {
                sb.append("Bill ").append(bill.getBillId())
                  .append(" (").append(bill.getType()).append(") due on ").append(bill.getDueDate()).append("\n");
            }
        }
        return sb.length() == 0 ? "No new notifications." : sb.toString();
    }
}