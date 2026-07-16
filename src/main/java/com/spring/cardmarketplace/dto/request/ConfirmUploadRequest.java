package com.spring.cardmarketplace.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmUploadRequest(
        @NotNull UUID imageId
) {
}
