package com.spring.cardmarketplace.Repositories;

import com.spring.cardmarketplace.Entities.Listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID>,
                                            JpaSpecificationExecutor<Listing> {

}
