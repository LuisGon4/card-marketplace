package com.spring.cardmarketplace.dto.response;

import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ListingDetailsResponse(
        UUID id,
        UUID cardId,
        String cardName,
        Condition condition,
        Printing printing,
        BigDecimal askingPrice,
        BigDecimal marketPrice,
        boolean priceFlagged,
        String location,
        String description,
        String sellerUsername,
        UUID sellerId,
        List<String> imageUrls
) {
}
