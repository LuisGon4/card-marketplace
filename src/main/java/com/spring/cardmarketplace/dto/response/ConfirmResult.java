package com.spring.cardmarketplace.dto.response;

public record ConfirmResult(
        ConfirmUploadResponse response,
        boolean created
) {
}
