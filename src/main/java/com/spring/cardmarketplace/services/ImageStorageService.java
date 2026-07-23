package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.dto.request.PresignUploadRequest;
import com.spring.cardmarketplace.dto.response.ConfirmResult;
import com.spring.cardmarketplace.dto.response.ConfirmUploadResponse;
import com.spring.cardmarketplace.dto.response.PresignUploadResponse;
import com.spring.cardmarketplace.configuration.S3Properties;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.ListingImage;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.*;
import com.spring.cardmarketplace.repositories.ListingImageRepository;
import com.spring.cardmarketplace.repositories.ListingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Duration PRESIGN_EXPIRY = Duration.ofMinutes(5);
    private static final long MAX_FILE_SIZE_BYTES = 5_242_880;
    private static final String CONTENT_TYPE_JPEG = "image/jpeg";
    private static final long MAX_NUMBER_OF_IMAGES = 5;
    private static final String STAGING_PREFIX = "staging/";
    private static final String LISTINGS_PREFIX = "listings/";
    private static final String JPEG_EXTENSION = ".jpg";

    private final CurrentUserProvider currentUserProvider;
    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public ImageStorageService(CurrentUserProvider currentUserProvider,
                               ListingRepository listingRepository,
                               S3Properties s3Properties,
                               S3Presigner s3Presigner,
                               ListingImageRepository listingImageRepository,
                               S3Client s3Client) {
        this.currentUserProvider = currentUserProvider;
        this.listingRepository = listingRepository;
        this.s3Properties = s3Properties;
        this.s3Presigner = s3Presigner;
        this.listingImageRepository = listingImageRepository;
        this.s3Client = s3Client;
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
            throw new InactiveListingException("Listing is not active with id: " + listingId);
        }

        if(request.fileSizeBytes() > MAX_FILE_SIZE_BYTES){
            throw new FileSizeLimitExceededException("File size exceeds maximum of " + MAX_FILE_SIZE_BYTES);
        }

        UUID imageId = UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(stagingKey(imageId))
                .contentType(CONTENT_TYPE_JPEG)
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

    public ConfirmResult confirm(UUID listingId, UUID imageId) {

        Listing listing = listingRepository.findById(listingId).orElseThrow(
                () -> new ListingNotFoundException("Listing not found with id: " + listingId)
        );

        User user = currentUserProvider.getCurrentUser();
        if (!user.getId().equals(listing.getSeller().getId())) {
            throw new ForbiddenOperationException(
                    "Not allowed to confirm an upload for a listing you don't own");
        }

        // Idempotency exit — only reachable after authorization passes
        Optional<ListingImage> existing = listingImageRepository.findById(imageId);
        if (existing.isPresent()) {
            ListingImage image = existing.get();

            if (!image.getListing().getId().equals(listingId)) {
                throw new UploadNotFoundException("No uploaded file found for imageId: " + imageId);
            }

            return new ConfirmResult(
                    new ConfirmUploadResponse(image.getId(), image.getPosition()),
                    false
            );
        }

        if (!listing.isActive()) {
            throw new InactiveListingException("Listing is not active with id: " + listingId);
        }

        long numOfImages = listingImageRepository.countByListing(listing);
        if (numOfImages >= MAX_NUMBER_OF_IMAGES) {
            throw new ImageCapacityExceededException(
                    "Image capacity exceeded for listing with id: " + listingId);
        }

        String stagingKey = stagingKey(imageId);

        HeadObjectResponse head;
        try {
            head = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(stagingKey)
                    .build());
        } catch (NoSuchKeyException e) {
            throw new UploadNotFoundException("No uploaded file found for imageId: " + imageId);
        }

        if (head.contentLength() > MAX_FILE_SIZE_BYTES
                || !CONTENT_TYPE_JPEG.equals(head.contentType())) {
            throw new UploadNotFoundException(
                    "Uploaded file does not match upload contract for imageId: " + imageId);
        }

        String finalKey = finalKey(listingId, imageId);

        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(s3Properties.bucket())
                .sourceKey(stagingKey)
                .destinationBucket(s3Properties.bucket())
                .destinationKey(finalKey)
                .build()
        );

        ListingImage savedImage;
        try {
            savedImage = listingImageRepository.save(
                    new ListingImage(imageId, listing, finalKey, (int) numOfImages));
        } catch (DataIntegrityViolationException e) {
            ListingImage winner = listingImageRepository.findById(imageId)
                    .orElseThrow(() -> e);
            return new ConfirmResult(
                    new ConfirmUploadResponse(winner.getId(), winner.getPosition()),
                    false
            );
        }

        return new ConfirmResult(
                new ConfirmUploadResponse(savedImage.getId(), savedImage.getPosition()),
                true
        );
    }

    private String stagingKey(UUID imageId) {
        return STAGING_PREFIX + imageId + JPEG_EXTENSION;
    }

    private String finalKey(UUID listingId, UUID imageId) {
        return LISTINGS_PREFIX + listingId + "/" + imageId + JPEG_EXTENSION;
    }
}
