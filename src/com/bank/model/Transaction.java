package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private static long nextId = 10001;
    private String transactionId;
    private String type;
    private double amount;
    private LocalDateTime timestamp;
    private String accountNumber;

    public Transaction(String type, double amount, Account account) {
        this.transactionId = String.valueOf(nextId++);
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.accountNumber = account.getAccountNumber();
    }

    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAccountNumber() { return accountNumber; }

    @Override
    public String toString() {
        return String.format("%s | %s | %.2f | %s", timestamp, type, amount, accountNumber);
    }
}