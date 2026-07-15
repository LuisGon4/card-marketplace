package com.spring.cardmarketplace.exception;

public class InactiveListingException extends RuntimeException {
    public InactiveListingException(String message) {
        super(message);
    }
}
