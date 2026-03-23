package com.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<Account> accounts;
    private List<Transaction> transactions;

    public Customer(String username, String password) {
        super(username, password);
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public List<Account> getAccounts() { return accounts; }
    public void addAccount(Account account) { accounts.add(account); }

    public List<Transaction> getTransactions() { return transactions; }
    public void addTransaction(Transaction t) { transactions.add(t); }
}