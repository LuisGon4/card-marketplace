package com.spring.cardmarketplace.dto.response;

import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;

import java.math.BigDecimal;
import java.util.UUID;

public record ValuationResponse(
        UUID cardId,
        Condition condition,
        Printing printing,
        BigDecimal marketPrice
) {}
