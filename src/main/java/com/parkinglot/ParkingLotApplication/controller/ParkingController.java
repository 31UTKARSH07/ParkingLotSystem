package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.dto.ExitRequest;
import com.parkinglot.ParkingLotApplication.dto.ExitResponse;
import com.parkinglot.ParkingLotApplication.dto.ParkingRequest;
import com.parkinglot.ParkingLotApplication.dto.ParkingResponse;
import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.services.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/available-spots/{parkingLotId}")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots(@PathVariable String parkingLotId) {
        List<ParkingSpot> spots = parkingService.getAvailableSpots(parkingLotId);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/available-count/{parkingLotId}")
    public ResponseEntity<Long> getAvailableSpotsCount(@PathVariable String parkingLotId) {
        long count = parkingService.getAvailableSpotsCount(parkingLotId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/find-vehicle/{licensePlate}")
    public ResponseEntity<ParkingSpot> findVehicleSpot(@PathVariable String licensePlate) {
        ParkingSpot spot = parkingService.findSpotByVehicle(licensePlate);
        if (spot == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spot);
    }

    @PostMapping("/lot")
    public ResponseEntity<ParkingLot> createParkingLot(@RequestBody ParkingLot parkingLot) {
        ParkingLot created = parkingService.createParkingLot(parkingLot);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}