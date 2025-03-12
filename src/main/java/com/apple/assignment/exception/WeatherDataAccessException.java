package com.apple.assignment.exception;

/**
 * Exception is thrown when weather api endpoint errors.
 */
public class WeatherDataAccessException extends Exception {
    public WeatherDataAccessException(String message) {
        super(message);
    }
}
