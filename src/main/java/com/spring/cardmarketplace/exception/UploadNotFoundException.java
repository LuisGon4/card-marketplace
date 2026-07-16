package com.spring.cardmarketplace.exception;

public class UploadNotFoundException extends RuntimeException {
    public UploadNotFoundException(String message) {
        super(message);
    }
}
