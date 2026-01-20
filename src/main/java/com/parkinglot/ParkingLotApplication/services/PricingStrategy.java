package com.parkinglot.ParkingLotApplication.services;


import com.parkinglot.ParkingLotApplication.model.enums.VehicleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PricingStrategy {

    @Value("${parking.rate.bike:10.0}")
    private double bikeRate;

    @Value("${parking.rate.car:20.0}")
    private double carRate;

    @Value("${parking.rate.truck:30.0}")
    private double truckRate;

    @Value("${parking.rate.van:25.0}")
    private double vanRate;

    @Value("${parking.grace.period:15}")
    private int gracePeriodMinutes;

    public double calculateParkingFee(VehicleType vehicleType, long durationInMinutes) {
        // Apply grace period
        if (durationInMinutes <= gracePeriodMinutes) {
            return 0.0;
        }

        // Calculate hours (rounded up)
        double hours = Math.ceil(durationInMinutes / 60.0);

        // Get hourly rate based on vehicle type
        double hourlyRate = getHourlyRate(vehicleType);

        return hours * hourlyRate;
    }

    private double getHourlyRate(VehicleType vehicleType) {
        return switch (vehicleType) {
            case BIKE -> bikeRate;
            case CAR -> carRate;
            case TRUCK -> truckRate;
            case VAN -> vanRate;
        };
    }
}