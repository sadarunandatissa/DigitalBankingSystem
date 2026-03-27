package com.bank.main;

import com.bank.model.*;
import com.bank.service.BillService;
import com.bank.service.LoanService;
import com.bank.util.BankData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

public class JavaFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BankData bankData = BankData.getInstance();
        try {
            bankData.loadData();
        } catch (Exception e) {
            System.out.println("No existing data, starting fresh.");
        }

        // Create default users if missing
        if (bankData.getAdmin("admin") == null) {
            bankData.addAdmin(new Admin("admin", "admin123"));
        }
        if (bankData.getStaff("staff1") == null) {
            bankData.addStaff(new Staff("staff1", "staff123"));
        }
        if (bankData.getCustomer("test") == null) {
            Customer test = new Customer("test", "1234");
            test.addAccount(new SavingsAccount(1000));
            test.addAccount(new CheckingAccount(200));
            BillService.addBill(test, "ELECTRICITY", 150.00, LocalDate.now().plusDays(5));
            BillService.addBill(test, "WATER", 80.00, LocalDate.now().plusDays(10));
            LoanService.applyForLoan(test, 5000, 8.5, 24);
            bankData.addCustomer(test);
        }

        // Shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                bankData.saveData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        Parent root = FXMLLoader.load(getClass().getResource("/com/bank/ui/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/bank/ui/style.css").toExternalForm());
        primaryStage.setTitle("Digital Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}