package com.bank.model;

public class CheckingAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 500;
    private static final double INTEREST_RATE = 0.01;

    public CheckingAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (balance - amount < -OVERDRAFT_LIMIT) {
            throw new Exception("Overdraft limit exceeded");
        }
        balance -= amount;
        addTransaction(new Transaction("WITHDRAWAL", amount, this));
    }

    @Override
    public double calculateInterest() {
        return Math.max(0, balance * INTEREST_RATE / 12);
    }
}