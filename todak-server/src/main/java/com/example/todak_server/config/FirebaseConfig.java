package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Bean
    public FirebaseApp firebaseApp() {
        try (InputStream serviceAccount =
                     getClass().getResourceAsStream(firebaseConfigPath.replace("classpath:", "/"))) {
            if (serviceAccount == null) {
                throw new IllegalStateException("Firebase config file not found at: " + firebaseConfigPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(storageBucket)
                    .build();

            // 이미 초기화돼 있으면 그대로 반환
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.getInstance();
            }

            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            throw new IllegalStateException("Firebase init failed: " + e.getMessage(), e);
        }
    }
}