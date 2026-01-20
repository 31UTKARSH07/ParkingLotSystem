package com.parkinglot.ParkingLotApplication.dto;

import com.parkinglot.ParkingLotApplication.model.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRequest {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private String color;

    private String ownerName;

    private String ownerPhone;

    @NotBlank(message = "Parking lot ID is required")
    private String parkingLotId;

    private Integer preferredFloor;
}