package com.bank.util;

import com.bank.model.*;
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
        if (instance == null) {
            instance = new BankData();
        }
        return instance;
    }

    // Customer operations
    public void addCustomer(Customer c) {
        customers.put(c.getUsername(), c);
    }

    public Customer getCustomer(String username) {
        return customers.get(username);
    }

    public boolean authenticateCustomer(String username, String password) {
        Customer c = customers.get(username);
        return c != null && c.getPassword().equals(password);
    }

    // Staff operations
    public void addStaff(Staff s) {
        staff.put(s.getUsername(), s);
    }

    public Staff getStaff(String username) {
        return staff.get(username);
    }

    public boolean authenticateStaff(String username, String password) {
        Staff s = staff.get(username);
        return s != null && s.getPassword().equals(password);
    }

    // Admin operations
    public void addAdmin(Admin a) {
        admins.put(a.getUsername(), a);
    }

    public Admin getAdmin(String username) {
        return admins.get(username);
    }

    public boolean authenticateAdmin(String username, String password) {
        Admin a = admins.get(username);
        return a != null && a.getPassword().equals(password);
    }

    // Get all customers (used by staff for loan approval)
    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    public Collection<Staff> getAllStaff() {
    return staff.values();
}

public Collection<Admin> getAllAdmins() {
    return admins.values();
}

    // Persistence
    public void saveData() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
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