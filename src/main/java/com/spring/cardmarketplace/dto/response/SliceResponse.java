package com.spring.cardmarketplace.dto.response;

import java.util.List;

public record SliceResponse<T>(
        List<T> content,
        boolean hasNext
) {
}
