package com.spring.cardmarketplace.dto.request;

import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateListingRequest(
        @NotNull UUID cardId,
        @NotNull Condition condition,
        @NotNull Printing printing,
        @NotNull @Positive BigDecimal askingPrice,
        @NotBlank String location,
        @Size(max = 250) String description
) {}