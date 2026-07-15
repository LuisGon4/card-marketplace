package com.spring.cardmarketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PresignUploadRequest (
        @Positive @NotNull Long fileSizeBytes
){
}
