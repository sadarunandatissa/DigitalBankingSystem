package com.bank.model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.03;  // 3% annual interest
    private static final double MIN_BALANCE = 500;

    public SavingsAccount(double initialBalance) {
        super(initialBalance);
        if (initialBalance < MIN_BALANCE) {
            throw new IllegalArgumentException("Minimum balance for Savings Account: " + MIN_BALANCE);
        }
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (balance - amount < MIN_BALANCE) {
            throw new Exception("Withdrawal would violate minimum balance requirement.");
        }
        super.withdraw(amount);
    }

    @Override
    public double calculateInterest() {
        // Monthly interest = balance * (annual rate / 12)
        return balance * INTEREST_RATE / 12;
    }
}