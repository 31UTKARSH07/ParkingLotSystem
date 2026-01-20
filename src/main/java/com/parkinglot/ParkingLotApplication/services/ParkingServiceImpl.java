package com.parkinglot.ParkingLotApplication.services;


import com.parkinglot.ParkingLotApplication.dto.ExitRequest;
import com.parkinglot.ParkingLotApplication.dto.ExitResponse;
import com.parkinglot.ParkingLotApplication.dto.ParkingRequest;
import com.parkinglot.ParkingLotApplication.dto.ParkingResponse;
import com.parkinglot.ParkingLotApplication.exception.NoSpotAvailableException;
import com.parkinglot.ParkingLotApplication.exception.ParkingLotNotFoundException;
import com.parkinglot.ParkingLotApplication.exception.TicketNotFoundException;
import com.parkinglot.ParkingLotApplication.model.ParkingLot;
import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.Ticket;
import com.parkinglot.ParkingLotApplication.model.Vehicle;
import com.parkinglot.ParkingLotApplication.model.enums.SpotStatus;
import com.parkinglot.ParkingLotApplication.model.enums.SpotType;
import com.parkinglot.ParkingLotApplication.model.enums.VehicleType;
import com.parkinglot.ParkingLotApplication.repository.ParkingLotRepository;
import com.parkinglot.ParkingLotApplication.repository.ParkingSpotRepository;
import com.parkinglot.ParkingLotApplication.repository.TicketRepository;
import com.parkinglot.ParkingLotApplication.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.parkinglot.ParkingLotApplication.model.enums.VehicleType.BIKE;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final TicketRepository ticketRepository;
    private final PricingStrategy pricingStrategy;

    @Override
    @Transactional
    public ParkingResponse parkVehicle(ParkingRequest request) {
        // Verify parking lot exists
        ParkingLot parkingLot = parkingLotRepository.findById(request.getParkingLotId())
                .orElseThrow(() -> new ParkingLotNotFoundException("Parking lot not found"));

        // Check if vehicle already exists, if not create new
        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
                .orElse(new Vehicle());

        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setOwnerPhone(request.getOwnerPhone());
        vehicle = vehicleRepository.save(vehicle);

        // Check if vehicle is already parked
        if (ticketRepository.findByVehicleIdAndExitTimeIsNull(vehicle.getId()).isPresent()) {
            return ParkingResponse.builder()
                    .success(false)
                    .message("Vehicle is already parked")
                    .build();
        }

        // Find suitable spot
        SpotType requiredSpotType = determineSpotType(request.getVehicleType());
        ParkingSpot spot = findAvailableSpot(request.getParkingLotId(), requiredSpotType);

        if (spot == null) {
            throw new NoSpotAvailableException("No available spots for " + request.getVehicleType());
        }

        // Occupy the spot
        spot.occupySpot(vehicle.getId());
        parkingSpotRepository.save(spot);

        // Generate ticket
        String ticketNumber = generateTicketNumber();
        Ticket ticket = new Ticket(ticketNumber, vehicle.getId(), spot.getId(), parkingLot.getId());
        ticketRepository.save(ticket);

        return ParkingResponse.builder()
                .success(true)
                .ticketNumber(ticketNumber)
                .spotNumber(spot.getSpotNumber())
                .floor(spot.getFloor())
                .vehicleLicensePlate(vehicle.getLicensePlate())
                .entryTime(ticket.getEntryTime())
                .message("Vehicle parked successfully")
                .build();
    }

    @Override
    @Transactional
    public ExitResponse exitVehicle(ExitRequest request) {
        // Find ticket
        Ticket ticket = ticketRepository.findByTicketNumber(request.getTicketNumber())
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + request.getTicketNumber()));

        if (ticket.getExitTime() != null) {
            throw new TicketNotFoundException("Ticket already processed");
        }

        // Get vehicle and spot
        Vehicle vehicle = vehicleRepository.findById(ticket.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        ParkingSpot spot = parkingSpotRepository.findById(ticket.getSpotId())
                .orElseThrow(() -> new RuntimeException("Spot not found"));

        // Calculate parking duration and amount
        ticket.setExitTime(LocalDateTime.now());
        long durationInMinutes = ticket.getParkedDurationInMinutes();
        double amount = pricingStrategy.calculateParkingFee(vehicle.getType(), durationInMinutes);
        ticket.setAmount(amount);
        ticket.setPaid(true);
        ticketRepository.save(ticket);

        // Free the spot
        spot.freeSpot();
        parkingSpotRepository.save(spot);

        return ExitResponse.builder()
                .success(true)
                .ticketNumber(ticket.getTicketNumber())
                .vehicleLicensePlate(vehicle.getLicensePlate())
                .entryTime(ticket.getEntryTime())
                .exitTime(ticket.getExitTime())
                .durationInMinutes(durationInMinutes)
                .amount(amount)
                .message("Payment successful. Thank you!")
                .build();
    }

    @Override
    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        return parkingLotRepository.save(parkingLot);
    }

    @Override
    public List<ParkingSpot> getAvailableSpots(String parkingLotId) {
        return parkingSpotRepository.findByParkingLotIdAndStatus(parkingLotId, SpotStatus.AVAILABLE);
    }

    @Override
    public long getAvailableSpotsCount(String parkingLotId) {
        return parkingSpotRepository.countByParkingLotIdAndStatus(parkingLotId, SpotStatus.AVAILABLE);
    }

    @Override
    public ParkingSpot findSpotByVehicle(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        return parkingSpotRepository.findByVehicleId(vehicle.getId())
                .orElse(null);
    }

    // Helper methods
    private SpotType determineSpotType(VehicleType vehicleType) {
        return switch (vehicleType) {
            case BIKE -> SpotType.SMALL;
            case CAR -> SpotType.MEDIUM;
            case TRUCK, VAN -> SpotType.LARGE;
        };
    }

    private ParkingSpot findAvailableSpot(String parkingLotId, SpotType spotType) {
        List<ParkingSpot> availableSpots = parkingSpotRepository
                .findByParkingLotIdAndTypeAndStatus(parkingLotId, spotType, SpotStatus.AVAILABLE);

        return availableSpots.isEmpty() ? null : availableSpots.get(0);
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}