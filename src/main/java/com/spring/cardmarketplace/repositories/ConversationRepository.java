package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Conversation;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByListingAndSellerAndBuyer(Listing listing, User seller, User buyer);

    @EntityGraph(attributePaths = {"listing", "listing.card", "buyer", "seller"})
    List<Conversation> findByBuyerOrSeller(User buyer, User seller);
}
