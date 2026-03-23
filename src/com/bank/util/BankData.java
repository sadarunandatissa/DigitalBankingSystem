package com.bank.util;

import java.io.*;
import java.util.*;

import com.bank.model.Customer;
import com.bank.model.Staff;
import com.bank.model.Admin;

public class BankData {
    private static BankData instance;
    private Map<String, Customer> customers;
    private Map<String, Staff> staff;
    private Map<String, Admin> admins;

    private BankData() {
        customers = new HashMap<>();
    }

    public static BankData getInstance() {
        if (instance == null) instance = new BankData();
        return instance;
    }

    public void addCustomer(Customer c) { customers.put(c.getUsername(), c); }
    public Customer getCustomer(String username) { return customers.get(username); }

    public void saveData() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/bankdata.dat"))) {
            oos.writeObject(customers);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
        File f = new File("data/bankdata.dat");
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            customers = (Map<String, Customer>) ois.readObject();
        }
    }
}