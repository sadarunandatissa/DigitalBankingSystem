package com.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<Account> accounts;
    private List<Transaction> transactions;
    private List<Object> loans;      // For Phase 4: will hold Loan objects
    private List<Object> bills;      // For Phase 4: will hold BillPayment objects

    public Customer(String username, String password) {
        super(username, password);
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.bills = new ArrayList<>();
    }

    public List<Account> getAccounts() { return accounts; }
    public void addAccount(Account account) { accounts.add(account); }

    public List<Transaction> getTransactions() { return transactions; }
    public void addTransaction(Transaction t) { transactions.add(t); }

    public List<Object> getLoans() { return loans; }
    public void addLoan(Object loan) { loans.add(loan); }

    public List<Object> getBills() { return bills; }
    public void addBill(Object bill) { bills.add(bill); }
}