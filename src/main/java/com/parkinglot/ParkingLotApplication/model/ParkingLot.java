package com.parkinglot.ParkingLotApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_lots")
public class ParkingLot {

    @Id
    private String id;

    private String name;
    private String address;
    private int totalFloors;

    // Capacity per floor for each spot type
    private Map<String, Integer> capacityPerFloor;

    // Pricing per hour for different vehicle types
    private Map<String, Double> hourlyRates;

    public ParkingLot(String name, String address, int totalFloors) {
        this.name = name;
        this.address = address;
        this.totalFloors = totalFloors;
        this.capacityPerFloor = new HashMap<>();
        this.hourlyRates = new HashMap<>();
        initializeDefaultRates();
    }

    private void initializeDefaultRates() {
        hourlyRates.put("BIKE", 10.0);
        hourlyRates.put("CAR", 20.0);
        hourlyRates.put("TRUCK", 30.0);
        hourlyRates.put("VAN", 25.0);
    }
}