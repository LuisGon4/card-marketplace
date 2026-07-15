package com.spring.cardmarketplace.exception;

public class FileSizeLimitExceededException extends RuntimeException {
    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}
