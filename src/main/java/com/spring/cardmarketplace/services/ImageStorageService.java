package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.dto.request.PresignUploadRequest;
import com.spring.cardmarketplace.dto.response.PresignUploadResponse;
import com.spring.cardmarketplace.configuration.S3Properties;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.FileSizeLimitExceededException;
import com.spring.cardmarketplace.exception.ForbiddenOperationException;
import com.spring.cardmarketplace.exception.ListingNotFoundException;
import com.spring.cardmarketplace.repositories.ListingRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Duration PRESIGN_EXPIRY = Duration.ofMinutes(5);
    private static final long MAX_FILE_SIZE_BYTES = 5_242_880;
    private static final String STAGING_PREFIX = "staging/";
    private static final String JPEG_EXTENSION = ".jpg";
    private static final String CONTENT_TYPE = "image/jpeg";

    private final CurrentUserProvider currentUserProvider;
    private final ListingRepository listingRepository;
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;

    public ImageStorageService(CurrentUserProvider currentUserProvider, ListingRepository listingRepository,  S3Properties s3Properties, S3Presigner s3Presigner) {
        this.currentUserProvider = currentUserProvider;
        this.listingRepository = listingRepository;
        this.s3Properties = s3Properties;
        this.s3Presigner = s3Presigner;
    }

    public PresignUploadResponse presignUpload(UUID listingId, PresignUploadRequest request){
        Listing listing = listingRepository.findById(listingId).orElseThrow(
                () -> new ListingNotFoundException("Listing not found with id: " + listingId)
        );

        User user = currentUserProvider.getCurrentUser();

        if(!listing.getSeller().getId().equals(user.getId())){
            throw new ForbiddenOperationException("You are not allowed to request an upload URL for a listing you don't own");
        }

        if(!listing.isActive()){
            throw new ForbiddenOperationException("Listing is not active");
        }

        if(request.fileSizeBytes() > MAX_FILE_SIZE_BYTES){
            throw new FileSizeLimitExceededException("File size exceeds maximum of " + MAX_FILE_SIZE_BYTES);
        }

        UUID imageId = UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(STAGING_PREFIX + imageId + JPEG_EXTENSION)
                .contentType(CONTENT_TYPE)
                .contentLength(request.fileSizeBytes())
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGN_EXPIRY)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

        return new PresignUploadResponse(
                presignedPutObjectRequest.url().toString(),
                imageId,
                request.fileSizeBytes()
        );
    }
}
