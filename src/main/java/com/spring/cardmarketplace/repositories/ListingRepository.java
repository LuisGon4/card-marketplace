package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Listing;

import com.spring.cardmarketplace.entities.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID>,
                                            JpaSpecificationExecutor<Listing> {
    @Override
    @EntityGraph(attributePaths = {"card", "seller"})
    List<Listing> findAll(Specification<Listing> spec);

    @EntityGraph(attributePaths = {"card", "seller"})
    Optional<Listing> findByIdAndActiveTrue(UUID listingId);

    @EntityGraph(attributePaths = {"card", "seller"})
    List<Listing> findBySellerOrderByCreatedAtDesc(User seller);
}
