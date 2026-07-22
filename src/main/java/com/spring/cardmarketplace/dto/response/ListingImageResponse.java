package com.spring.cardmarketplace.dto.response;

import com.spring.cardmarketplace.dto.request.ListingImageDto;

import java.util.List;

public record ListingImageResponse(
        List<ListingImageDto> images
) {
}
