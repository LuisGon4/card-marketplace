package com.spring.cardmarketplace.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        boolean hasNext,
        long totalElements,
        int totalPages
) {
}
