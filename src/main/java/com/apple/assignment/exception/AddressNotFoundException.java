package com.apple.assignment.exception;

/**
 * Exception thrown when the address is not found.
 */
public class AddressNotFoundException extends Exception {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
