package com.spring.cardmarketplace.dto.response;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        String content,
        Instant sentAt
) {}
