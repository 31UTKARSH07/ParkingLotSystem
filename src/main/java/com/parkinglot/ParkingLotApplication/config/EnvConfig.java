package com.parkinglot.ParkingLotApplication.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnvVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Won't fail if .env is missing
                    .load();

            // Set system properties from .env file
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("✓ Environment variables loaded from .env file");
        } catch (Exception e) {
            System.out.println("⚠ No .env file found, using default configuration");
        }
    }
}