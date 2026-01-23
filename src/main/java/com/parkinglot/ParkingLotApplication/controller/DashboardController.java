package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.model.ParkingSpot;
import com.parkinglot.ParkingLotApplication.model.Ticket;
import com.parkinglot.ParkingLotApplication.model.enums.SpotStatus;
import com.parkinglot.ParkingLotApplication.repository.ParkingLotRepository;
import com.parkinglot.ParkingLotApplication.repository.ParkingSpotRepository;
import com.parkinglot.ParkingLotApplication.repository.TicketRepository;
import com.parkinglot.ParkingLotApplication.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;
    private final TicketRepository ticketRepository;

    @GetMapping("/overview/{parkingLotId}")
    public ResponseEntity<Map<String, Object>> getDashboardOverview(@PathVariable String parkingLotId) {
        Map<String, Object> overview = new HashMap<>();

        // Parking lot info
        parkingLotRepository.findById(parkingLotId).ifPresent(lot -> {
            overview.put("parkingLotName", lot.getName());
            overview.put("totalFloors", lot.getTotalFloors());
        });

        // Spot statistics
        List<ParkingSpot> allSpots = parkingSpotRepository.findByParkingLotId(parkingLotId);
        long totalSpots = allSpots.size();
        long availableSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
                .count();
        long occupiedSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.OCCUPIED)
                .count();

        overview.put("totalSpots", totalSpots);
        overview.put("availableSpots", availableSpots);
        overview.put("occupiedSpots", occupiedSpots);
        overview.put("occupancyRate", totalSpots > 0 ? (occupiedSpots * 100.0 / totalSpots) : 0);

        // Active vehicles
        long activeVehicles = ticketRepository.countByParkingLotIdAndExitTimeIsNull(parkingLotId);
        overview.put("activeVehicles", activeVehicles);

        // Today's revenue
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        List<Ticket> todaysTickets = ticketRepository.findByEntryTimeBetween(startOfDay, endOfDay);
        double todaysRevenue = todaysTickets.stream()
                .filter(Ticket::isPaid)
                .mapToDouble(Ticket::getAmount)
                .sum();
        overview.put("todaysRevenue", todaysRevenue);
        overview.put("todaysTickets", todaysTickets.size());

        return ResponseEntity.ok(overview);
    }

    @GetMapping("/revenue/{parkingLotId}")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @PathVariable String parkingLotId,
            @RequestParam(required = false) String period) {

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        // Determine date range based on period
        switch (period != null ? period.toLowerCase() : "today") {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = LocalDate.now().atStartOfDay();
        }

        List<Ticket> tickets = ticketRepository.findByEntryTimeBetween(startDate, endDate)
                .stream()
                .filter(ticket -> ticket.getParkingLotId().equals(parkingLotId))
                .toList();

        double totalRevenue = tickets.stream()
                .filter(Ticket::isPaid)
                .mapToDouble(Ticket::getAmount)
                .sum();

        double pendingRevenue = tickets.stream()
                .filter(ticket -> !ticket.isPaid())
                .mapToDouble(ticket -> ticket.getAmount() != null ? ticket.getAmount() : 0)
                .sum();

        Map<String, Object> revenue = new HashMap<>();
        revenue.put("period", period != null ? period : "today");
        revenue.put("totalRevenue", totalRevenue);
        revenue.put("pendingRevenue", pendingRevenue);
        revenue.put("totalTickets", tickets.size());
        revenue.put("paidTickets", tickets.stream().filter(Ticket::isPaid).count());
        revenue.put("unpaidTickets", tickets.stream().filter(ticket -> !ticket.isPaid()).count());

        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/floor-wise/{parkingLotId}")
    public ResponseEntity<Map<Integer, Map<String, Object>>> getFloorWiseStatistics(
            @PathVariable String parkingLotId) {

        List<ParkingSpot> allSpots = parkingSpotRepository.findByParkingLotId(parkingLotId);
        Map<Integer, Map<String, Object>> floorStats = new HashMap<>();

        // Group by floor
        allSpots.stream()
                .collect(java.util.stream.Collectors.groupingBy(ParkingSpot::getFloor))
                .forEach((floor, spots) -> {
                    Map<String, Object> stats = new HashMap<>();
                    long total = spots.size();
                    long available = spots.stream()
                            .filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
                            .count();
                    long occupied = spots.stream()
                            .filter(spot -> spot.getStatus() == SpotStatus.OCCUPIED)
                            .count();

                    stats.put("totalSpots", total);
                    stats.put("availableSpots", available);
                    stats.put("occupiedSpots", occupied);
                    stats.put("occupancyRate", total > 0 ? (occupied * 100.0 / total) : 0);

                    floorStats.put(floor, stats);
                });

        return ResponseEntity.ok(floorStats);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSystemSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalParkingLots", parkingLotRepository.count());
        summary.put("totalVehicles", vehicleRepository.count());
        summary.put("totalTickets", ticketRepository.count());
        summary.put("activeTickets", ticketRepository.findByIsPaid(false).size());

        return ResponseEntity.ok(summary);
    }
}