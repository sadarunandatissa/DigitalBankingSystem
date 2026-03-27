package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;

public class BankService {
    public static void transfer(Account source, Account destination, double amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
        try {
            source.withdraw(amount);
            destination.deposit(amount);
            source.addTransaction(new Transaction("TRANSFER_OUT", amount, source));
            destination.addTransaction(new Transaction("TRANSFER_IN", amount, destination));
        } catch (Exception e) {
            throw new InsufficientFundsException("Transfer failed: " + e.getMessage());
        }
    }
}