package com.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<Account> accounts;
    private List<Transaction> transactions;
    private List<Loan> loans;
    private List<BillPayment> bills;

    public Customer(String username, String password) {
        super(username, password);
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.bills = new ArrayList<>();
    }

    // Getters and adders
    public List<Account> getAccounts() { return accounts; }
    public void addAccount(Account account) { accounts.add(account); }

    public List<Transaction> getTransactions() { return transactions; }
    public void addTransaction(Transaction t) { transactions.add(t); }

    public List<Loan> getLoans() { return loans; }
    public void addLoan(Loan loan) { loans.add(loan); }

    public List<BillPayment> getBills() { return bills; }
    public void addBill(BillPayment bill) { bills.add(bill); }
}