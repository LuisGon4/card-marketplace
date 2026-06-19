package com.spring.cardmarketplace.Repositories;

import com.spring.cardmarketplace.Entities.Conversation;
import com.spring.cardmarketplace.Entities.Listing;
import com.spring.cardmarketplace.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByListingAndSellerAndBuyer(Listing listing, User seller, User buyer);
}
