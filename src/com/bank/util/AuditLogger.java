package com.bank.util;

import java.io.*;
import java.time.LocalDateTime;

public class AuditLogger {
    private static final String LOG_FILE = "logs/invalid_ops.log";

    static {
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdir();
    }

    public static void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(LocalDateTime.now() + " - " + message);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}