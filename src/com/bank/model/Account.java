package com.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {
    private static long nextId = 1001;
    private String accountNumber;
    protected double balance;
    protected List<Transaction> transactions;

    public Account(double initialBalance) {
        this.accountNumber = String.valueOf(nextId++);
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
        addTransaction(new Transaction("DEPOSIT", amount, this));
    }

    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new Exception("Insufficient funds");
        balance -= amount;
        addTransaction(new Transaction("WITHDRAWAL", amount, this));
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public abstract double calculateInterest();

    public List<Transaction> getTransactions() { return transactions; }
}