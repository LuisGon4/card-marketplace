package com.spring.cardmarketplace.dto.request;

import java.util.UUID;

public record ListingImageDto(
        UUID id,
        String url,
        int position
) {
}
