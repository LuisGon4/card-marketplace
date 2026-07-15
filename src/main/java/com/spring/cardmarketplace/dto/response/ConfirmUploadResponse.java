package com.spring.cardmarketplace.dto.response;

import java.util.UUID;

public record ConfirmUploadResponse(
        UUID imageId,
        int position
) {
}
