package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingImageRepository extends JpaRepository<ListingImage, UUID> {
    long countByListing(Listing listing);

    List<ListingImage> findByListingIdOrderByPositionAsc(UUID listingId);

    List<ListingImage> findByListingInAndPosition(List<Listing> listings, int position);

    Optional<ListingImage> findByListingIdAndPosition(UUID listingId, int position);

    //List<ListingImage> findByListingOrderByPositionAsc(Listing listing)
}
