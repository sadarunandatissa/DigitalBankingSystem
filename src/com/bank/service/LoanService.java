package com.bank.service;

import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.util.AuditLogger;

public class LoanService {

    // Customer applies for loan
    public static void applyForLoan(Customer customer, double amount, double interestRate, int termMonths) {
        Loan loan = new Loan(amount, interestRate, termMonths);
        customer.addLoan(loan);
        AuditLogger.log("Loan application submitted by " + customer.getUsername() + " for $" + amount);
    }

    // Staff approves a pending loan
    public static boolean approveLoan(Loan loan) {
        if (loan.getStatus().equals("PENDING")) {
            loan.setStatus("APPROVED");
            AuditLogger.log("Loan " + loan.getLoanId() + " approved.");
            return true;
        }
        return false;
    }

    // Staff rejects a pending loan
    public static boolean rejectLoan(Loan loan) {
        if (loan.getStatus().equals("PENDING")) {
            loan.setStatus("REJECTED");
            AuditLogger.log("Loan " + loan.getLoanId() + " rejected.");
            return true;
        }
        return false;
    }
}