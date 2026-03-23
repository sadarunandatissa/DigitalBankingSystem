package com.bank.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BillPayment implements Serializable {
    private static long nextId = 7001;
    private String billId;
    private String type;           // "ELECTRICITY", "WATER", "INTERNET", etc.
    private double amount;
    private LocalDate dueDate;
    private boolean paid;

    public BillPayment(String type, double amount, LocalDate dueDate) {
        this.billId = String.valueOf(nextId++);
        this.type = type;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = false;
    }

    // Getters and setters
    public String getBillId() { return billId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override
    public String toString() {
        return String.format("Bill %s: %s $%.2f due %s %s",
                billId, type, amount, dueDate, paid ? "(PAID)" : "(UNPAID)");
    }
}