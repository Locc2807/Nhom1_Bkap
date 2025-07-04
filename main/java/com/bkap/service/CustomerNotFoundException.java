package com.bkap.service;

public class CustomerNotFoundException extends RuntimeException {
    
    // Constructor accepting a message
    public CustomerNotFoundException(String message) {
        super(message);
    }

    // Optionally, you can add another constructor that accepts both a message and a cause
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
