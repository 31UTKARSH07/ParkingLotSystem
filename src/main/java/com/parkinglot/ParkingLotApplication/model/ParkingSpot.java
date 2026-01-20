package com.parkinglot.ParkingLotApplication.model;


import com.parkinglot.ParkingLotApplication.model.enums.SpotStatus;
import com.parkinglot.ParkingLotApplication.model.enums.SpotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_spots")
public class ParkingSpot {

    @Id
    private String id;

    private String spotNumber;
    private SpotType type;
    private SpotStatus status;
    private int floor;
    private String parkingLotId;

    // Currently parked vehicle
    private String vehicleId;

    public ParkingSpot(String spotNumber, SpotType type, int floor, String parkingLotId) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.floor = floor;
        this.parkingLotId = parkingLotId;
        this.status = SpotStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return this.status == SpotStatus.AVAILABLE;
    }

    public void occupySpot(String vehicleId) {
        this.vehicleId = vehicleId;
        this.status = SpotStatus.OCCUPIED;
    }

    public void freeSpot() {
        this.vehicleId = null;
        this.status = SpotStatus.AVAILABLE;
    }
}