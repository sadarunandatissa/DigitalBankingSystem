package com.bank.model;

import java.time.LocalDate;

public class FixedDepositAccount extends Account {
    private LocalDate maturityDate;
    private double interestRate;
    private static final double PENALTY_RATE = 0.02;

    public FixedDepositAccount(double initialBalance, int termMonths) {
        super(initialBalance);
        this.maturityDate = LocalDate.now().plusMonths(termMonths);
        this.interestRate = 0.07;
    }

    public LocalDate getMaturityDate() { return maturityDate; }

    @Override
    public void withdraw(double amount) throws Exception {
        if (LocalDate.now().isBefore(maturityDate)) {
            double penalty = balance * PENALTY_RATE;
            balance -= penalty;
            addTransaction(new Transaction("EARLY_WITHDRAWAL_PENALTY", penalty, this));
        }
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new Exception("Insufficient funds");
        balance -= amount;
        addTransaction(new Transaction("WITHDRAWAL", amount, this));
    }

    @Override
    public double calculateInterest() {
        return balance * interestRate / 12;
    }
}