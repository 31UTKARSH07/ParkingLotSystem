package com.parkinglot.ParkingLotApplication.repository;

import com.parkinglot.ParkingLotApplication.model.Vehicle;
import com.parkinglot.ParkingLotApplication.model.enums.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByType(VehicleType type);

    List<Vehicle> findByOwnerPhone(String ownerPhone);

    boolean existsByLicensePlate(String licensePlate);
}