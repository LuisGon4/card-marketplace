package com.spring.cardmarketplace.dto.response;

import java.util.UUID;

public record PresignUploadResponse(
        String uploadUrl,
        UUID imageId,
        Long fileSizeBytes) {}
