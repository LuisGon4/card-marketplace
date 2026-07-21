package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.ConfirmUploadRequest;
import com.spring.cardmarketplace.dto.request.PresignUploadRequest;
import com.spring.cardmarketplace.dto.response.ConfirmResult;
import com.spring.cardmarketplace.dto.response.ConfirmUploadResponse;
import com.spring.cardmarketplace.dto.response.PresignUploadResponse;
import com.spring.cardmarketplace.services.ImageStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ConfirmUploadResponse> confirmUpload(@PathVariable UUID listingId,
                                                               @Valid @RequestBody ConfirmUploadRequest request){
        ConfirmResult result = imageStorageService.confirm(listingId, request.imageId());
        return ResponseEntity
                .status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(result.response());
    }
}
