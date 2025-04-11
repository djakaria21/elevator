package com.bluestaq.elevator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ElevatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElevatorApplication.class, args);
	}

}
