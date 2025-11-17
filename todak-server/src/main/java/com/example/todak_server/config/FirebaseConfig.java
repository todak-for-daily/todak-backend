package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");

            if (firebaseConfigPath == null || firebaseConfigPath.isBlank()) {
                throw new IllegalStateException("FIREBASE_CONFIG_PATH is missing");
            }

            InputStream serviceAccount = new FileInputStream(firebaseConfigPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(storageBucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("[INFO] Firebase initialized from " + firebaseConfigPath);
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }

        } catch (Exception e) {
            throw new IllegalStateException("Firebase initialization failed: " + e.getMessage(), e);
        }
    }
}

