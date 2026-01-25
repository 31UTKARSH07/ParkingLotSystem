package com.parkinglot.ParkingLotApplication.controller;

import com.parkinglot.ParkingLotApplication.dto.JwtResponse;
import com.parkinglot.ParkingLotApplication.dto.LoginRequest;
import com.parkinglot.ParkingLotApplication.dto.MessageResponse;
import com.parkinglot.ParkingLotApplication.dto.RegisterRequest;
import com.parkinglot.ParkingLotApplication.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String message = authService.register(registerRequest);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}