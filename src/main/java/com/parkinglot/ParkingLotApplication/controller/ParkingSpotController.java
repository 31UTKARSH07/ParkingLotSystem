package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.enums.SpotStatus;
import com.parkinglot.ParkingLotApplication.model.enums.SpotType;
import com.parkinglot.ParkingLotApplication.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotRepository parkingSpotRepository;

    @GetMapping("/available/{parkingLotId}")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots(@PathVariable String parkingLotId) {
        List<ParkingSpot> spots = parkingSpotRepository.findByParkingLotIdAndStatus(
                parkingLotId, SpotStatus.AVAILABLE);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/available/count/{parkingLotId}")
    public ResponseEntity<Long> getAvailableSpotsCount(@PathVariable String parkingLotId) {
        long count = parkingSpotRepository.countByParkingLotIdAndStatus(
                parkingLotId, SpotStatus.AVAILABLE);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/available/{parkingLotId}/type/{spotType}")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpotsByType(
            @PathVariable String parkingLotId,
            @PathVariable SpotType spotType) {
        List<ParkingSpot> spots = parkingSpotRepository.findByParkingLotIdAndTypeAndStatus(
                parkingLotId, spotType, SpotStatus.AVAILABLE);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/floor/{parkingLotId}/{floor}")
    public ResponseEntity<List<ParkingSpot>> getSpotsByFloor(
            @PathVariable String parkingLotId,
            @PathVariable int floor) {
        List<ParkingSpot> spots = parkingSpotRepository.findByParkingLotIdAndFloor(parkingLotId, floor);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/{spotNumber}")
    public ResponseEntity<ParkingSpot> getSpotByNumber(@PathVariable String spotNumber) {
        return parkingSpotRepository.findBySpotNumber(spotNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all/{parkingLotId}")
    public ResponseEntity<List<ParkingSpot>> getAllSpots(@PathVariable String parkingLotId) {
        List<ParkingSpot> spots = parkingSpotRepository.findByParkingLotId(parkingLotId);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/statistics/{parkingLotId}")
    public ResponseEntity<Map<String, Object>> getSpotStatistics(@PathVariable String parkingLotId) {
        List<ParkingSpot> allSpots = parkingSpotRepository.findByParkingLotId(parkingLotId);

        long totalSpots = allSpots.size();
        long availableSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
                .count();
        long occupiedSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.OCCUPIED)
                .count();

        Map<String, Object> statistics = Map.of(
                "totalSpots", totalSpots,
                "availableSpots", availableSpots,
                "occupiedSpots", occupiedSpots,
                "occupancyRate", totalSpots > 0 ? (occupiedSpots * 100.0 / totalSpots) : 0
        );

        return ResponseEntity.ok(statistics);
    }
}