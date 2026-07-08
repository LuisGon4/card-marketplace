package com.spring.cardmarketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank String content
) {}
