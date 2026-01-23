package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import com.parkinglot.ParkingLotApplication.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotRepository parkingLotRepository;

    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(@RequestBody ParkingLot parkingLot) {
        ParkingLot created = parkingLotRepository.save(parkingLot);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
        List<ParkingLot> parkingLots = parkingLotRepository.findAll();
        return ResponseEntity.ok(parkingLots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable String id) {
        return parkingLotRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ParkingLot> getParkingLotByName(@PathVariable String name) {
        return parkingLotRepository.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> updateParkingLot(
            @PathVariable String id,
            @RequestBody ParkingLot parkingLot) {
        return parkingLotRepository.findById(id)
                .map(existing -> {
                    parkingLot.setId(id);
                    ParkingLot updated = parkingLotRepository.save(parkingLot);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable String id) {
        if (parkingLotRepository.existsById(id)) {
            parkingLotRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}