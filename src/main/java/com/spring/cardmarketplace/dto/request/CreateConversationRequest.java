package com.spring.cardmarketplace.dto.request;

import java.util.UUID;

public record CreateConversationRequest (
        UUID listingId
){}
