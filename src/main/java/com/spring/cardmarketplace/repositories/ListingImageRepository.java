package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface ListingImageRepository extends JpaRepository<ListingImage, UUID> {
    long countByListing(Listing listing);
}
