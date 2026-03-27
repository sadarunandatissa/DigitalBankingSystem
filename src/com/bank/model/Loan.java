package com.bank.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Loan implements Serializable {
    private static long nextId = 5001;
    private String loanId;
    private double amount;
    private double interestRate;
    private int termMonths;
    private double monthlyPayment;
    private LocalDate startDate;
    private LocalDate nextDueDate;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, PAID

    public Loan(double amount, double interestRate, int termMonths) {
        this.loanId = String.valueOf(nextId++);
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.startDate = LocalDate.now();
        this.nextDueDate = startDate.plusMonths(1);
        this.status = "PENDING";
        calculateMonthlyPayment();
    }

    private void calculateMonthlyPayment() {
        double monthlyRate = interestRate / 12 / 100;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        this.monthlyPayment = amount * monthlyRate * factor / (factor - 1);
    }

    public String getLoanId() { return loanId; }
    public double getAmount() { return amount; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public double getMonthlyPayment() { return monthlyPayment; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void makePayment() {
        if (status.equals("APPROVED") || status.equals("ACTIVE")) {
            nextDueDate = nextDueDate.plusMonths(1);
            if (nextDueDate.isAfter(startDate.plusMonths(termMonths))) {
                status = "PAID";
            } else {
                status = "ACTIVE";
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Loan %s: $%.2f @ %.2f%% for %d months, status: %s",
                loanId, amount, interestRate, termMonths, status);
    }
}