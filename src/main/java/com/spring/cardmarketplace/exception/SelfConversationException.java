package com.spring.cardmarketplace.exception;

public class SelfConversationException extends RuntimeException {
    public SelfConversationException(String message) {
        super(message);
    }
}
