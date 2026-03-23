package com.bank.main;

import com.bank.model.*;
import com.bank.service.*;
import com.bank.util.*;
import java.time.LocalDate;
import java.util.Scanner;

public class LoginManager {
    private static Scanner scanner = new Scanner(System.in);
    private static BankData bankData = BankData.getInstance();

    public static void main(String[] args) {
        // Load existing data
        try {
            bankData.loadData();
            System.out.println("Data loaded.");
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh.");
        }

        // Create default admin if none exists
        if (bankData.getAdmin("admin") == null) {
            Admin defaultAdmin = new Admin("admin", "admin123");
            bankData.addAdmin(defaultAdmin);
            System.out.println("Default admin created: username=admin, password=admin123");
        }

        // Create default staff if none exists
        if (bankData.getStaff("staff1") == null) {
            Staff defaultStaff = new Staff("staff1", "staff123");
            bankData.addStaff(defaultStaff);
            System.out.println("Default staff created: username=staff1, password=staff123");
        }

        // Create a test customer with accounts, loans, and bills for demo
        Customer test = bankData.getCustomer("test");
        if (test == null) {
            test = new Customer("test", "1234");
            // Add accounts
            SavingsAccount savings = new SavingsAccount(1000);
            CheckingAccount checking = new CheckingAccount(200);
            test.addAccount(savings);
            test.addAccount(checking);

            // Add sample bills
            BillService.addBill(test, "ELECTRICITY", 150.00, LocalDate.now().plusDays(5));
            BillService.addBill(test, "WATER", 80.00, LocalDate.now().plusDays(10));
            BillService.addBill(test, "INTERNET", 60.00, LocalDate.now().minusDays(2)); // overdue

            // Add a sample pending loan (optional)
            LoanService.applyForLoan(test, 5000, 8.5, 24);

            bankData.addCustomer(test);
            System.out.println("Test customer created: username=test, password=1234");
        }

        // Main login loop
        while (true) {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            // Check each role
            Customer customer = bankData.getCustomer(username);
            if (customer != null && customer.getPassword().equals(password)) {
                new CustomerDashboard(customer).show();
                continue;
            }

            Staff staff = bankData.getStaff(username);
            if (staff != null && staff.getPassword().equals(password)) {
                new StaffDashboard(staff).show();
                continue;
            }

            Admin admin = bankData.getAdmin(username);
            if (admin != null && admin.getPassword().equals(password)) {
                new AdminDashboard(admin).show();
                continue;
            }

            AuditLogger.log("Failed login attempt for user: " + username);
            System.out.println("Invalid credentials. Try again.");
        }
    }
}