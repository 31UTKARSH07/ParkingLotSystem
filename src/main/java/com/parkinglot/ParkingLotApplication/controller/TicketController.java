package com.parkinglot.ParkingLotApplication.controller;


import com.parkinglot.ParkingLotApplication.model.Ticket;
import com.parkinglot.ParkingLotApplication.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<Ticket> getTicketByNumber(@PathVariable String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Ticket>> getTicketsByVehicle(@PathVariable String vehicleId) {
        List<Ticket> tickets = ticketRepository.findByVehicleId(vehicleId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/parking-lot/{parkingLotId}")
    public ResponseEntity<List<Ticket>> getTicketsByParkingLot(@PathVariable String parkingLotId) {
        List<Ticket> tickets = ticketRepository.findByParkingLotId(parkingLotId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Ticket>> getUnpaidTickets() {
        List<Ticket> tickets = ticketRepository.findByIsPaid(false);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<Ticket>> getPaidTickets() {
        List<Ticket> tickets = ticketRepository.findByIsPaid(true);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/active/{parkingLotId}")
    public ResponseEntity<Long> getActiveTicketsCount(@PathVariable String parkingLotId) {
        long count = ticketRepository.countByParkingLotIdAndExitTimeIsNull(parkingLotId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Ticket>> getTicketsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Ticket> tickets = ticketRepository.findByEntryTimeBetween(start, end);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/vehicle/{vehicleId}/active")
    public ResponseEntity<Ticket> getActiveTicketByVehicle(@PathVariable String vehicleId) {
        return ticketRepository.findByVehicleIdAndExitTimeIsNull(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}