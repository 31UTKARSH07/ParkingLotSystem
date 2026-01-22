package com.parkinglot.ParkingLotApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExitRequest {

    @NotBlank(message = "Ticket number is required")
    private String ticketNumber;
}

