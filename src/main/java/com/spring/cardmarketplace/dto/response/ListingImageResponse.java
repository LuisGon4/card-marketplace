package com.spring.cardmarketplace.dto.response;

import java.util.List;

public record ListingImageResponse(
        List<ListingImageDto> images
) {
}
