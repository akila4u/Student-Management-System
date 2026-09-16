package com.SMSystem;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    private static Firestore db;

    public static void initialize() {
        if (db != null) return;
        try {
            FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            db = FirestoreClient.getFirestore();
            System.out.println(">>> Firebase Cloud Firestore connected successfully! <<<");
        } catch (IOException e) {
            System.err.println("Firebase Init Failed: " + e.getMessage());
        }
    }

    public static Firestore getDB() {
        if (db == null) initialize();
        return db;
    }
}