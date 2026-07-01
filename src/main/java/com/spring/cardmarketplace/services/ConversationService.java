package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.dto.request.CreateConversationRequest;
import com.spring.cardmarketplace.dto.response.ConversationResponse;
import com.spring.cardmarketplace.entities.Conversation;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.ListingNotFoundException;
import com.spring.cardmarketplace.exception.SelfConversationException;
import com.spring.cardmarketplace.repositories.ConversationRepository;
import com.spring.cardmarketplace.repositories.ListingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ListingRepository listingRepository;
    private final CurrentUserProvider currentUserProvider;
    private ConversationService self;


    public ConversationService(ConversationRepository conversationRepository, ListingRepository listingRepository, CurrentUserProvider currentUserProvider) {
        this.conversationRepository = conversationRepository;
        this.listingRepository = listingRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Autowired
    public void setSelf(@Lazy ConversationService self){
        this.self = self;
    }

    public ConversationResponse startWithRetry(CreateConversationRequest request){
        try{
            return self.start(request);
        } catch(DataIntegrityViolationException e){
            // the rare race: another request committed the row between our
            // pre-check and our insert. That transaction has now rolled back.
            // Retry once — a fresh proxied call gets a fresh transaction,
            // and this time the pre-check finds the committed row.
            return self.start(request);
        }
    }

    @Transactional
    public ConversationResponse start(CreateConversationRequest request){
        User currentUser = currentUserProvider.getCurrentUser();

        Listing listing = listingRepository.findById(request.listingId())
                .orElseThrow(() -> new ListingNotFoundException(
                        "Listing not found with id: " + request.listingId()));

        User seller = listing.getSeller();

        if(currentUser.getId().equals(seller.getId())){
            throw new SelfConversationException("Can't create conversation with self");
        }

        // Pre-Check: Does conversation already exist?
        Optional<Conversation> existing =
                conversationRepository.findByListingAndSellerAndBuyer(listing, seller, currentUser);
        if(existing.isPresent()){
            return toResponse(existing.get(), currentUser);
        }

        // Create path
        Conversation conversation = new Conversation();
        conversation.setSeller(seller);
        conversation.setBuyer(currentUser);
        conversation.setListing(listing);

        Conversation saved = conversationRepository.save(conversation);

        return toResponse(saved, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getForUser(){
        User currentUser = currentUserProvider.getCurrentUser();
        return conversationRepository.findByBuyerOrSeller(currentUser, currentUser)
                .stream()
                .map(c -> toResponse(c, currentUser)) // Convert each conversation to a toResponse
                .toList(); // Return as list
    }

    private ConversationResponse toResponse(Conversation c, User currentUser){
        User other = c.getBuyer().getId().equals(currentUser.getId()) ? c.getSeller() : c.getBuyer();

        return new ConversationResponse(
                c.getId(),
                c.getListing().getId(),
                c.getListing().getCard().getCardName(),
                other.getUsername(),
                c.getCreatedAt()
        );
    }
}
