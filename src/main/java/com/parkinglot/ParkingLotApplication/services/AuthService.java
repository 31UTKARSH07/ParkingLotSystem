package com.parkinglot.ParkingLotApplication.services;

import com.parkinglot.ParkingLotApplication.config.JwtUtil;
import com.parkinglot.ParkingLotApplication.dto.JwtResponse;
import com.parkinglot.ParkingLotApplication.dto.LoginRequest;
import com.parkinglot.ParkingLotApplication.dto.RegisterRequest;
import com.parkinglot.ParkingLotApplication.model.User;
import com.parkinglot.ParkingLotApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        return new JwtResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set default role if not provided
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            user.setRoles(Arrays.asList("ROLE_USER"));
        } else {
            user.setRoles(registerRequest.getRoles());
        }

        user.setEnabled(true);

        userRepository.save(user);
        return "User registered successfully";
    }
}