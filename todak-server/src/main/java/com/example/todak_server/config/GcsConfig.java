package com.example.todak_server.config;

import com.google.cloud.storage.Storage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GcsConfig {

    @Value("${gcp.storage.credentials.location}")
    private String credentialsPath;

    @Value("${gcp.storage.project-id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        InputStream keyStream = new ClassPathResource(credentialsPath.substring("classpath:".length())).getInputStream();

        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyStream))
                .setProjectId(projectId)
                .build()
                .getService();
    }
}
