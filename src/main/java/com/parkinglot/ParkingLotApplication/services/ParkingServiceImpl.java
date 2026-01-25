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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

        System.out.println("PARKING LOT FOUND: " + parkingLot);

        // Check if vehicle already exists, if not create new
        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.getLicensePlate())
                .orElse(new Vehicle());

        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setOwnerPhone(request.getOwnerPhone());
        vehicle = vehicleRepository.save(vehicle);

        System.out.println("VEHICLE FOUND: " + vehicle);

        // Check if vehicle is already parked
        if (ticketRepository.findByVehicleIdAndExitTimeIsNull(vehicle.getId()).isPresent()) {
            return ParkingResponse.builder()
                    .success(false)
                    .message("Vehicle is already parked")
                    .build();
        }

        // Find suitable spot
        SpotType requiredSpotType = determineSpotType(request.getVehicleType());

        System.out.println("REQUIRED_SPOT_TYPE: " + requiredSpotType);
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
    @Transactional
    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        ParkingLot savedLot = parkingLotRepository.save(parkingLot);
        initializeParkingSpots(savedLot);
        return savedLot;
    }

    private void initializeParkingSpots(ParkingLot parkingLot) {
        if (parkingLot.getCapacityPerFloor() == null)
            return;

        Map<SpotType, Integer> spotsPerType = new EnumMap<>(SpotType.class);

        parkingLot.getCapacityPerFloor().forEach((k, v) -> {
            try {
                VehicleType vType = VehicleType.valueOf(k.toUpperCase());
                SpotType sType = determineSpotType(vType);
                spotsPerType.merge(sType, v, Integer::sum);
            } catch (IllegalArgumentException e) {
                try {
                    SpotType sType = SpotType.valueOf(k.toUpperCase());
                    spotsPerType.merge(sType, v, Integer::sum);
                } catch (Exception ex) {
                    // Ignore invalid types
                }
            }
        });

        List<ParkingSpot> spots = new ArrayList<>();

        for (int floor = 1; floor <= parkingLot.getTotalFloors(); floor++) {
            for (Map.Entry<SpotType, Integer> entry : spotsPerType.entrySet()) {
                SpotType type = entry.getKey();
                int count = entry.getValue();

                for (int i = 1; i <= count; i++) {
                    // Unique ID: LotID-Floor-TypeInitial-Index
                    String spotNumber = String.format("%s-%d-%s-%d",
                            parkingLot.getId(), floor, type.name().substring(0, 1), i);

                    ParkingSpot spot = new ParkingSpot(
                            spotNumber,
                            type,
                            floor,
                            parkingLot.getId());
                    spots.add(spot);
                }
            }
        }
        parkingSpotRepository.saveAll(spots);
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