package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Conversation;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByListingAndSellerAndBuyer(Listing listing, User seller, User buyer);
}
