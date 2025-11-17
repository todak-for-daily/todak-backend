//package com.example.todak_server.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.*;
//
//@Configuration
//public class FirebaseConfig {
//
//    @Value("${firebase.storage.bucket}")
//    private String storageBucket;
//
//    @Bean
//    public FirebaseApp firebaseApp() {
//        try {
//            String firebaseConfig = System.getProperty("FIREBASE_CONFIG");
//            String firebaseConfigPath = System.getProperty("FIREBASE_CONFIG_PATH");
//
//            InputStream serviceAccount;
//
//            if (firebaseConfig != null && !firebaseConfig.isBlank()) {
//                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG (env variable)");
//                serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes());
//            } else if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
//                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG_PATH: " + firebaseConfigPath);
//                serviceAccount = new FileInputStream(firebaseConfigPath);
//            } else {
//                throw new IllegalStateException("Firebase config not found (neither FIREBASE_CONFIG nor FIREBASE_CONFIG_PATH)");
//            }
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setStorageBucket(storageBucket)
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                System.out.println("[INFO] FirebaseApp bean created and initialized!");
//                return FirebaseApp.initializeApp(options);
//            } else {
//                System.out.println("[INFO] FirebaseApp bean already exists. Returning existing instance.");
//                return FirebaseApp.getInstance();
//            }
//
//        } catch (Exception e) {
//            throw new IllegalStateException("Firebase initialization failed: " + e.getMessage(), e);
//        }
//    }
//}

package com.example.todak_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    // 예: firebase.config.path: classpath:firebase/serviceAccountKey.json
    @Value("${firebase.config.path:}")
    private String firebaseConfigPathFromYml;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            // 1) env → 2) system property 순으로 체크
            String firebaseConfig = firstNonBlank(
                    System.getenv("FIREBASE_CONFIG"),
                    System.getProperty("FIREBASE_CONFIG")
            );

            String firebaseConfigPath = firstNonBlank(
                    System.getenv("FIREBASE_CONFIG_PATH"),
                    System.getProperty("FIREBASE_CONFIG_PATH")
            );

            InputStream serviceAccount;

            if (firebaseConfig != null && !firebaseConfig.isBlank()) {
                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG (env/system JSON)");
                serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            } else if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
                System.out.println("[INFO] Initializing Firebase from FIREBASE_CONFIG_PATH (env/system): " + firebaseConfigPath);
                serviceAccount = new FileInputStream(firebaseConfigPath);

            } else if (firebaseConfigPathFromYml != null && !firebaseConfigPathFromYml.isBlank()) {
                String path = firebaseConfigPathFromYml.replace("classpath:", "");
                System.out.println("[INFO] Initializing Firebase from firebase.config.path (classpath): " + path);
                Resource resource = new ClassPathResource(path);
                serviceAccount = resource.getInputStream();

            } else {
                throw new IllegalStateException(
                        "Firebase config not found (FIREBASE_CONFIG, FIREBASE_CONFIG_PATH, firebase.config.path 모두 없음)"
                );
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

    // 작은 유틸
    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
