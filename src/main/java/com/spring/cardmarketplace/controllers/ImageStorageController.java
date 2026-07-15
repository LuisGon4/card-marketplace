package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.PresignUploadRequest;
import com.spring.cardmarketplace.dto.response.PresignUploadResponse;
import com.spring.cardmarketplace.services.ImageStorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/listings/{listingId}/images")
public class ImageStorageController {
    ImageStorageService imageStorageService;

    public ImageStorageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/presign")
    public PresignUploadResponse presign(@PathVariable UUID listingId,
                                          @Valid @RequestBody PresignUploadRequest request){
        return imageStorageService.presignUpload(listingId, request);
    }

    @PostMapping
    public ConfirmUploadResponse confirmUpload(@NotNull UUID imageId){

    }
}
