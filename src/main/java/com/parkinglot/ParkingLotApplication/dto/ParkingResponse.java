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
public class ParkingResponse {

    private String ticketNumber;
    private String spotNumber;
    private int floor;
    private String vehicleLicensePlate;
    private LocalDateTime entryTime;
    private String message;
    private boolean success;
}