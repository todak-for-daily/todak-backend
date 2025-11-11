package com.example.todak_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodakServerApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory(System.getProperty("user.dir"))  // 루트에서 .env 읽도록 강제
				.ignoreIfMissing()
				.load();

		String vertexKey = dotenv.get("VERTEX_API_KEY");
		String firebaseConfigPath = dotenv.get("FIREBASE_CONFIG_PATH");
		String firebaseConfig = dotenv.get("FIREBASE_CONFIG");
		
		System.out.println("[DEBUG] Working dir: " + System.getProperty("user.dir"));
		System.out.println("[DEBUG] .env Firebase path = " + firebaseConfigPath);

		if (vertexKey != null) {
			System.setProperty("VERTEX_API_KEY", vertexKey);
		} else {
			System.err.println("[WARN] VERTEX_API_KEY not found in .env");
		}

		if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
			System.setProperty("FIREBASE_CONFIG_PATH", firebaseConfigPath);
			System.out.println("[INFO] Using local Firebase config path: " + firebaseConfigPath);
		} else if (firebaseConfig != null && !firebaseConfig.isBlank()) {
			System.setProperty("FIREBASE_CONFIG", firebaseConfig);
			System.out.println("[INFO] Using Firebase config from environment variable");
		} else {
			System.err.println("[ERROR] Firebase config not found in environment");
		}

		SpringApplication.run(TodakServerApplication.class, args);
	}
}
