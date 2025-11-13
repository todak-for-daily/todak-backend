package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.*;

@Configuration
@Profile("!test")  // test 프로필에서는 이 Bean 등록 안 함
public class FirebaseConfig {

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Bean
    public FirebaseApp firebaseApp() {
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
                    .setStorageBucket(storageBucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("[INFO] FirebaseApp bean created and initialized!");
                return FirebaseApp.initializeApp(options);
            } else {
                System.out.println("[INFO] FirebaseApp bean already exists. Returning existing instance.");
                return FirebaseApp.getInstance();
            }

        } catch (Exception e) {
            throw new IllegalStateException("Firebase initialization failed: " + e.getMessage(), e);
        }
    }
}
