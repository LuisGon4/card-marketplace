package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.dto.request.CreateListingRequest;
import com.spring.cardmarketplace.dto.request.ListingFilter;
import com.spring.cardmarketplace.dto.request.UpdateListingRequest;
import com.spring.cardmarketplace.dto.response.ListingResponse;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.CardNotFoundException;
import com.spring.cardmarketplace.exception.ForbiddenOperationException;
import com.spring.cardmarketplace.exception.ListingNotFoundException;
import com.spring.cardmarketplace.repositories.CardRepository;
import com.spring.cardmarketplace.repositories.ListingRepository;
import com.spring.cardmarketplace.repositories.ListingSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private final CardRepository cardRepository;
    private final CurrentUserProvider currentUserProvider;

    public ListingService(ListingRepository listingRepository, CardRepository cardRepository, CurrentUserProvider currentUserProvider){
        this.listingRepository = listingRepository;
        this.cardRepository = cardRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public ListingResponse findById(UUID listingId){
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(
                        "No listing found with id: " + listingId
                ));

        return toResponse(listing);
    }

    public ListingResponse create(CreateListingRequest request){
        Card card = cardRepository.findById(request.cardId()).
                orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + request.cardId()
                ));

        User seller = currentUserProvider.getCurrentUser();

        Listing listing = new Listing();
        listing.setCard(card);
        listing.setSeller(seller);
        listing.setCondition(request.condition());
        listing.setPrinting(request.printing());
        listing.setAskingPrice(request.askingPrice());
        listing.setLocation(request.location());
        listing.setDescription(request.description());
        listing.setActive(true);

        Listing saved = listingRepository.save(listing);

        return toResponse(saved);
    }

    public ListingResponse update(UUID listingId, UpdateListingRequest request){
        Listing listing = listingRepository.findById(listingId).
                orElseThrow(() -> new ListingNotFoundException(
                        "No listing found with id: " + listingId));

        User currentUser = currentUserProvider.getCurrentUser();

        if(!listing.getSeller().getId().equals(currentUser.getId())){
            throw new ForbiddenOperationException(
                    "You are not the seller of this listing"
            );
        }

        if(request.askingPrice() != null){
            listing.setAskingPrice(request.askingPrice());
        }

        if(request.description() != null) {
            listing.setDescription(request.description());
        }

        Listing updated = listingRepository.save(listing);

        return toResponse(updated);
    }

    public void delete(UUID listingId){
        Listing listing = listingRepository.findById(listingId).
                orElseThrow(() -> new ListingNotFoundException(
                        "No listing found with id: " + listingId
                ));

        User currentUser = currentUserProvider.getCurrentUser();

        if(!listing.getSeller().getId().equals(currentUser.getId())){
            throw new ForbiddenOperationException(
                    "You are not the seller of this listing"
            );
        }

        listing.setActive(false);

        listingRepository.save(listing);
    }

    public List<ListingResponse> findAll(ListingFilter filter){
        Specification<Listing> spec = ListingSpecifications.isActive();

        if(filter.cardName() != null){
            spec = spec.and(ListingSpecifications.hasCardName(filter.cardName()));
        }

        if(filter.setName() != null){
            spec = spec.and(ListingSpecifications.hasSetName(filter.setName()));
        }

        if(filter.condition() != null){
            spec = spec.and(ListingSpecifications.hasCondition(filter.condition()));
        }

        if(filter.printing() != null){
            spec = spec.and(ListingSpecifications.hasPrinting(filter.printing()));
        }

        if(filter.minPrice() != null && filter.maxPrice() != null){
            spec = spec.and(ListingSpecifications.priceBetween(filter.minPrice(), filter.maxPrice()));
        } else if(filter.minPrice() != null){
            spec = spec.and(ListingSpecifications.priceAbove(filter.minPrice()));
        } else if(filter.maxPrice() != null) {
            spec = spec.and(ListingSpecifications.priceBelow(filter.maxPrice()));
        }

        return listingRepository.findAll(spec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ListingResponse toResponse(Listing listing){
        return new ListingResponse(
                listing.getId(),
                listing.getCard().getId(),
                listing.getCard().getCardName(),
                listing.getCondition(),
                listing.getPrinting(),
                listing.getAskingPrice(),
                listing.getMarketPrice(),
                listing.isPriceFlagged(),
                listing.getLocation(),
                listing.getDescription(),
                listing.getSeller().getUsername(),
                listing.getSeller().getId()
        );
    }
}
