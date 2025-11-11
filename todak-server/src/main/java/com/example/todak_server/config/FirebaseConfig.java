package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            String firebaseConfig = System.getProperty("FIREBASE_CONFIG");
            String firebaseConfigPath = System.getProperty("FIREBASE_CONFIG_PATH");

            InputStream serviceAccount;

            if (firebaseConfig != null && !firebaseConfig.isBlank()) {
                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG (env variable)");
                serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes());
            } else if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG_PATH: " + firebaseConfigPath);
                serviceAccount = new FileInputStream(firebaseConfigPath);
            } else {
                throw new IllegalStateException("Firebase config not found (neither FIREBASE_CONFIG nor FIREBASE_CONFIG_PATH)");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            }

        } catch (Exception e) {
            throw new IllegalStateException("Firebase initialization failed: " + e.getMessage(), e);
        }
    }
}
