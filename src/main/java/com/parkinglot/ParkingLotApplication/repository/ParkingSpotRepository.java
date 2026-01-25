package com.parkinglot.ParkingLotApplication.repository;

import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.enums.SpotStatus;
import com.parkinglot.ParkingLotApplication.model.enums.SpotType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends MongoRepository<ParkingSpot, String> {

    List<ParkingSpot> findByParkingLotId(String parkingLotId);

    List<ParkingSpot> findByParkingLotIdAndStatus(String parkingLotId, SpotStatus status);

    @Query("{ 'parkingLotId': ?0, 'type': ?1, 'status': ?2 }")
    List<ParkingSpot> findByParkingLotIdAndTypeAndStatus(
            String parkingLotId,
            SpotType type,
            SpotStatus status
    );

    Optional<ParkingSpot> findBySpotNumber(String spotNumber);

    Optional<ParkingSpot> findByVehicleId(String vehicleId);

    long countByParkingLotIdAndStatus(String parkingLotId, SpotStatus status);

    List<ParkingSpot> findByParkingLotIdAndFloor(String parkingLotId, int floor);
}