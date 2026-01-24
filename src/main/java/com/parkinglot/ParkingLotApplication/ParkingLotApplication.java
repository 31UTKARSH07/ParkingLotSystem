package com.parkinglot.ParkingLotApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ParkingLotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingLotApplication.class, args);
	}
}
