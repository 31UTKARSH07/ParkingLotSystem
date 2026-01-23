package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.dto.ExitRequest;
import com.parkinglot.ParkingLotApplication.dto.ExitResponse;
import com.parkinglot.ParkingLotApplication.dto.ParkingRequest;
import com.parkinglot.ParkingLotApplication.dto.ParkingResponse;
import com.parkinglot.ParkingLotApplication.services.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/park")
    public ResponseEntity<ParkingResponse> parkVehicle(@Valid @RequestBody ParkingRequest request) {
        ParkingResponse response = parkingService.parkVehicle(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PostMapping("/exit")
    public ResponseEntity<ExitResponse> exitVehicle(@Valid @RequestBody ExitRequest request) {
        ExitResponse response = parkingService.exitVehicle(request);
        return ResponseEntity.ok(response);
    }
}