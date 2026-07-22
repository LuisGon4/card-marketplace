package com.spring.cardmarketplace.dto.response;

import java.util.UUID;

public record ListingImageDto(
        UUID id,
        String url,
        int position
) {
}
