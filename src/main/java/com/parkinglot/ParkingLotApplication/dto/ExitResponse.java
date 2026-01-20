package com.parkinglot.ParkingLotApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExitResponse {

    private String ticketNumber;
    private String vehicleLicensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long durationInMinutes;
    private double amount;
    private String message;
    private boolean success;
}