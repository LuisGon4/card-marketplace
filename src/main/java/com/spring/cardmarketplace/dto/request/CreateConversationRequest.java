package com.spring.cardmarketplace.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateConversationRequest (
        @NotNull UUID listingId
){}
