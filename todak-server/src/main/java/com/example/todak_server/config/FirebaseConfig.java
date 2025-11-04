package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initFirebase() throws IOException {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream(
                    firebaseConfigPath.replace("classpath:", "/")
            );

            if (serviceAccount == null) {
                throw new IllegalStateException("Firebase config file not found");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase app initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Firebase app initialization failed: " + e.getMessage());
        }
    }
}
