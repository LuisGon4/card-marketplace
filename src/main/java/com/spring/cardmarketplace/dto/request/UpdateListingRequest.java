package com.spring.cardmarketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateListingRequest(
        @NotNull @Positive BigDecimal askingPrice,
        @Size(max = 250) String description
) {}
