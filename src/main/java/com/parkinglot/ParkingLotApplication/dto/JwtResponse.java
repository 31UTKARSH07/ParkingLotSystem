package com.parkinglot.ParkingLotApplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// JWT Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, String username, String email, List<String> roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}

