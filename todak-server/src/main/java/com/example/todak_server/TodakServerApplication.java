package com.example.todak_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodakServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodakServerApplication.class, args);
	}

}
