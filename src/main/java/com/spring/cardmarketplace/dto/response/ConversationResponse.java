package com.spring.cardmarketplace.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        UUID listingId,
        String cardName, // From listing.card, "what is this thread about"
        String otherUsername, // The participant who isn't the current user
        Instant createdAt
) {}
