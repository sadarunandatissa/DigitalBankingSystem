package com.bank.util;

import java.io.*;
import java.util.*;

public class BankData {
    private static BankData instance;
    private Map<String, Customer> customers;
    private Map<String, Staff> staff;
    private Map<String, Admin> admins;

    private BankData() {
        customers = new HashMap<>();
        staff = new HashMap<>();
        admins = new HashMap<>();
    }

    public static BankData getInstance() {
        if (instance == null) instance = new BankData();
        return instance;
    }

    public void addCustomer(Customer c) { customers.put(c.getUsername(), c); }
    public Customer getCustomer(String username) { return customers.get(username); }
    // similar for staff, admin

    public void saveData() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/bankdata.dat"))) {
            oos.writeObject(customers);
            oos.writeObject(staff);
            oos.writeObject(admins);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
        File f = new File("data/bankdata.dat");
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            customers = (Map<String, Customer>) ois.readObject();
            staff = (Map<String, Staff>) ois.readObject();
            admins = (Map<String, Admin>) ois.readObject();
        }
    }
}