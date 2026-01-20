package com.parkinglot.ParkingLotApplication.repository;


import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends MongoRepository<ParkingLot, String> {

    Optional<ParkingLot> findByName(String name);

    boolean existsByName(String name);
}