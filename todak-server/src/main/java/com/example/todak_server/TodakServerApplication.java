package com.example.todak_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodakServerApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("VERTEX_API_KEY", dotenv.get("VERTEX_API_KEY"));

		SpringApplication.run(TodakServerApplication.class, args);
	}

}
