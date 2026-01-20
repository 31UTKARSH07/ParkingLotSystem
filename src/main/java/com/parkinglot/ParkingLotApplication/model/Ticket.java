package com.parkinglot.ParkingLotApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;

    private String ticketNumber;
    private String vehicleId;
    private String spotId;
    private String parkingLotId;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private Double amount;
    private boolean isPaid;

    public Ticket(String ticketNumber, String vehicleId, String spotId, String parkingLotId) {
        this.ticketNumber = ticketNumber;
        this.vehicleId = vehicleId;
        this.spotId = spotId;
        this.parkingLotId = parkingLotId;
        this.entryTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public long getParkedDurationInMinutes() {
        if (exitTime == null) {
            return java.time.Duration.between(entryTime, LocalDateTime.now()).toMinutes();
        }
        return java.time.Duration.between(entryTime, exitTime).toMinutes();
    }
}