package com.udacity.vehicles.api;

public class CarError extends RuntimeException {

    public CarError(String message) {
        super(message);
    }

    public CarError(String message, Throwable cause) {
        super(message, cause);
    }

}
