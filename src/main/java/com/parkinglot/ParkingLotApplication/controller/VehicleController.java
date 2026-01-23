package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.Vehicle;
import com.parkinglot.ParkingLotApplication.model.enums.VehicleType;
import com.parkinglot.ParkingLotApplication.repository.ParkingSpotRepository;
import com.parkinglot.ParkingLotApplication.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @PostMapping
    public ResponseEntity<Vehicle> registerVehicle(@RequestBody Vehicle vehicle) {
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Vehicle saved = vehicleRepository.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/license/{licensePlate}")
    public ResponseEntity<Vehicle> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{vehicleType}")
    public ResponseEntity<List<Vehicle>> getVehiclesByType(@PathVariable VehicleType vehicleType) {
        List<Vehicle> vehicles = vehicleRepository.findByType(vehicleType);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/owner/{phone}")
    public ResponseEntity<List<Vehicle>> getVehiclesByOwnerPhone(@PathVariable String phone) {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerPhone(phone);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/location/{licensePlate}")
    public ResponseEntity<Map<String, Object>> findVehicleLocation(@PathVariable String licensePlate) {
        // Find vehicle by license plate
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByLicensePlate(licensePlate);

        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle vehicle = vehicleOpt.get();


        Optional<ParkingSpot> spotOpt = parkingSpotRepository.findByVehicleId(vehicle.getId());

        // Build response
        Map<String, Object> location = new HashMap<>();
        location.put("vehicle", vehicle);

        if (spotOpt.isPresent()) {
            ParkingSpot spot = spotOpt.get();
            location.put("isParked", true);
            location.put("spotNumber", spot.getSpotNumber());
            location.put("floor", spot.getFloor());
            location.put("spotType", spot.getType());
        } else {
            location.put("isParked", false);
            location.put("message", "Vehicle is not currently parked");
        }

        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable String id,
            @RequestBody Vehicle vehicle) {
        return vehicleRepository.findById(id)
                .map(existing -> {
                    vehicle.setId(id);
                    Vehicle updated = vehicleRepository.save(vehicle);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}