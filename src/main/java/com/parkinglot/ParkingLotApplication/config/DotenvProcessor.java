package com.parkinglot.ParkingLotApplication.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DotenvProcessor implements EnvironmentPostProcessor {
    
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            String userDir = System.getProperty("user.dir");
            System.out.println("=== DotenvProcessor Debug ===");
            System.out.println("Current directory: " + userDir);
            
            File envFile = new File(userDir, ".env");
            System.out.println(".env file exists: " + envFile.exists());
            System.out.println(".env file path: " + envFile.getAbsolutePath());
            
            if (!envFile.exists()) {
                System.err.println("ERROR: .env file not found!");
                return;
            }
            
            Dotenv dotenv = Dotenv
                    .configure()
                    .directory(userDir)
                    .filename(".env")
                    .ignoreIfMalformed()
                    .load();
            
            Map<String, Object> map = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                String value =  entry.getValue();
                System.out.println("Loaded: " + entry.getKey() + " = " + value);
                map.put(entry.getKey(), entry.getValue());
            });
            
            if (!map.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", map));
                System.out.println("✅ Successfully loaded " + map.size() + " properties from .env");
            } else {
                System.err.println("⚠️ No properties loaded from .env");
            }
            
            System.out.println("=== End DotenvProcessor Debug ===");
            
        } catch (Exception e) {
            System.err.println("ERROR in DotenvProcessor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
