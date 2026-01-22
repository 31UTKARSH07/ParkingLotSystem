package com.parkinglot.ParkingLotApplication.config;


import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.enums.SpotType;
import com.parkinglot.ParkingLotApplication.repository.ParkingLotRepository;
import com.parkinglot.ParkingLotApplication.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (parkingLotRepository.count() > 0) {
            System.out.println("Data already initialized. Skipping...");
            return;
        }

        System.out.println("Initializing sample parking lot data...");

        // Create a sample parking lot
        ParkingLot parkingLot = new ParkingLot("City Center Parking", "123 Main Street, Bangalore", 3);
        parkingLot = parkingLotRepository.save(parkingLot);

        // Create parking spots
        List<ParkingSpot> spots = new ArrayList<>();

        // Floor 1: 10 small, 15 medium, 5 large
        spots.addAll(createSpots(parkingLot.getId(), 1, SpotType.SMALL, 10, "A"));
        spots.addAll(createSpots(parkingLot.getId(), 1, SpotType.MEDIUM, 15, "B"));
        spots.addAll(createSpots(parkingLot.getId(), 1, SpotType.LARGE, 5, "C"));

        // Floor 2: 10 small, 15 medium, 5 large
        spots.addAll(createSpots(parkingLot.getId(), 2, SpotType.SMALL, 10, "A"));
        spots.addAll(createSpots(parkingLot.getId(), 2, SpotType.MEDIUM, 15, "B"));
        spots.addAll(createSpots(parkingLot.getId(), 2, SpotType.LARGE, 5, "C"));

        // Floor 3: 10 small, 15 medium, 5 large
        spots.addAll(createSpots(parkingLot.getId(), 3, SpotType.SMALL, 10, "A"));
        spots.addAll(createSpots(parkingLot.getId(), 3, SpotType.MEDIUM, 15, "B"));
        spots.addAll(createSpots(parkingLot.getId(), 3, SpotType.LARGE, 5, "C"));

        parkingSpotRepository.saveAll(spots);

        System.out.println("✓ Sample parking lot created: " + parkingLot.getName());
        System.out.println("✓ Total parking spots created: " + spots.size());
        System.out.println("✓ Parking Lot ID: " + parkingLot.getId());
        System.out.println("\nYou can now use this ID to park vehicles!");
    }

    private List<ParkingSpot> createSpots(String parkingLotId, int floor, SpotType type, int count, String prefix) {
        List<ParkingSpot> spots = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String spotNumber = String.format("%s%d-%03d", prefix, floor, i);
            spots.add(new ParkingSpot(spotNumber, type, floor, parkingLotId));
        }
        return spots;
    }
}