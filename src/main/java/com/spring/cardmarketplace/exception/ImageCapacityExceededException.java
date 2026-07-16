package com.spring.cardmarketplace.exception;

public class ImageCapacityExceededException extends RuntimeException {
    public ImageCapacityExceededException(String message) {
        super(message);
    }
}
