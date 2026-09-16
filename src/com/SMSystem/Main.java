package com.SMSystem;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Step 1: Firebase Cloud Firestore Initialize කිරීම
        try {
            FirebaseInitializer.initialize();
        } catch (Exception e) {
            System.err.println("Database connection warning: " + e.getMessage());
        }

        // Step 2: JavaFX Login Application එක Launch කිරීම
        Application.launch(LoginApp.class, args);
    }
}