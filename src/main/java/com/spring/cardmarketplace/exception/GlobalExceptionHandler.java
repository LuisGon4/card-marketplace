package com.spring.cardmarketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ForbiddenOperationException.class)
    private ResponseEntity<String> handleForbidden(ForbiddenOperationException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    private ResponseEntity<String> handleCardNotFound(CardNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ListingNotFoundException.class)
    private ResponseEntity<String> handleListingNotFound(ListingNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SelfConversationException.class)
    private ResponseEntity<String> handleSelfConversation(SelfConversationException ex){
        return ResponseEntity.status(HttpStatus.valueOf(422)).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    private ResponseEntity<String> handleUnauthenticated(UnauthenticatedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<String> handleUserNotFound(UserNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Malformed request body or invalid field value.");
    }
}
