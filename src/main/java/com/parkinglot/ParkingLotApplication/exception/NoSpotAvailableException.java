package com.parkinglot.ParkingLotApplication.exception;

public class NoSpotAvailableException extends RuntimeException {

    public NoSpotAvailableException(String message) {
        super(message);
    }

    public NoSpotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}