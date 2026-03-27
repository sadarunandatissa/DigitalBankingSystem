package com.bank.model;

public class StudentAccount extends Account {
    private static final double INTEREST_RATE = 0.02;
    private static final double WITHDRAWAL_LIMIT = 500;

    public StudentAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > WITHDRAWAL_LIMIT) {
            throw new Exception("Withdrawal exceeds limit of " + WITHDRAWAL_LIMIT);
        }
        if (amount > balance) throw new Exception("Insufficient funds");
        balance -= amount;
        addTransaction(new Transaction("WITHDRAWAL", amount, this));
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE / 12;
    }
}