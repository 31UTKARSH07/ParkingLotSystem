package com.parkinglot.ParkingLotApplication.services;



import com.parkinglot.ParkingLotApplication.dto.ExitRequest;
import com.parkinglot.ParkingLotApplication.dto.ExitResponse;
import com.parkinglot.ParkingLotApplication.dto.ParkingRequest;
import com.parkinglot.ParkingLotApplication.dto.ParkingResponse;
import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import com.parkinglot.ParkingLotApplication.model.ParkingSpot;

import java.util.List;

public interface ParkingService {

    ParkingResponse parkVehicle(ParkingRequest request);

    ExitResponse exitVehicle(ExitRequest request);

    ParkingLot createParkingLot(ParkingLot parkingLot);

    List<ParkingSpot> getAvailableSpots(String parkingLotId);

    long getAvailableSpotsCount(String parkingLotId);

    ParkingSpot findSpotByVehicle(String licensePlate);
}