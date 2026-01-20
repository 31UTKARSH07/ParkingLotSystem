package com.parkinglot.ParkingLotApplication.repository;


import com.parkinglot.ParkingLotApplication.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByVehicleId(String vehicleId);

    List<Ticket> findByParkingLotId(String parkingLotId);

    List<Ticket> findByIsPaid(boolean isPaid);

    List<Ticket> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Ticket> findByVehicleIdAndExitTimeIsNull(String vehicleId);

    long countByParkingLotIdAndExitTimeIsNull(String parkingLotId);
}