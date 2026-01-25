package com.parkinglot.ParkingLotApplication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userProfile() {
        return "User Profile - This is a protected endpoint";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Admin Dashboard - Only admins can access this";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint - No authentication required";
    }
}