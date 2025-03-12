package com.apple.assignment.exception;

/**
 * Exception is thrown when the GEOCODE api has errored.
 */
public class GeoCodeDataAccessException    extends Exception {
    public GeoCodeDataAccessException(String message) {
        super(message);
    }
}
