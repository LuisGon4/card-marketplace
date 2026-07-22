package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.configuration.S3Properties;
import com.spring.cardmarketplace.dto.request.CreateListingRequest;
import com.spring.cardmarketplace.dto.request.ListingFilter;
import com.spring.cardmarketplace.dto.response.ListingDetailsResponse;
import com.spring.cardmarketplace.dto.request.UpdateListingRequest;
import com.spring.cardmarketplace.dto.response.ListingSummaryResponse;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.Listing;
import com.spring.cardmarketplace.entities.ListingImage;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.CardNotFoundException;
import com.spring.cardmarketplace.exception.ForbiddenOperationException;
import com.spring.cardmarketplace.exception.ListingNotFoundException;
import com.spring.cardmarketplace.repositories.CardRepository;
import com.spring.cardmarketplace.repositories.ListingImageRepository;
import com.spring.cardmarketplace.repositories.ListingRepository;
import com.spring.cardmarketplace.repositories.ListingSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListingService {
    private static final BigDecimal FLAG_MULTIPLIER = new BigDecimal("1.5");
    private static final int THUMBNAIL_POSITION = 0;

    private final ListingRepository listingRepository;
    private final CardRepository cardRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CardPricingService cardPricingService;
    private final ListingImageRepository listingImageRepository;
    private final S3Properties s3Properties;

    public ListingService(ListingRepository listingRepository,
                          CardRepository cardRepository,
                          CurrentUserProvider currentUserProvider,
                          CardPricingService cardPricingService,
                          ListingImageRepository listingImageRepository,
                          S3Properties s3Properties) {
        this.listingRepository = listingRepository;
        this.cardRepository = cardRepository;
        this.currentUserProvider = currentUserProvider;
        this.cardPricingService = cardPricingService;
        this.listingImageRepository = listingImageRepository;
        this.s3Properties = s3Properties;
    }

    public ListingDetailsResponse findById(UUID listingId){
        Listing listing = listingRepository.findByIdAndActiveTrue(listingId)
                .orElseThrow(() -> new ListingNotFoundException(
                        "No listing found with id: " + listingId
                ));

        List<String> imageUrls = listingImageRepository
                .findByListingIdOrderByPositionAsc(listingId)
                .stream()
                .map(img -> buildUrl(img.getImageKey()))
                .toList();

        return toDetailResponse(listing, imageUrls);
    }





    public ListingSummaryResponse create(CreateListingRequest request){
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

        applyMarketPrice(listing, card);
        Listing saved = listingRepository.save(listing);

        return toSummaryResponse(saved, null);
    }





    public ListingSummaryResponse update(UUID listingId, UpdateListingRequest request){
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

        return toSummaryResponse(updated, null);
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





    public List<ListingSummaryResponse> findAll(ListingFilter filter){
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

        List<Listing> listings = listingRepository.findAll(spec);
        if (listings.isEmpty()) {
            return List.of();
        }

        Map<UUID, String> thumbnailKeys = listingImageRepository
                .findByListingInAndPosition(listings, THUMBNAIL_POSITION)
                .stream()
                .collect(Collectors.toMap(
                        img -> img.getListing().getId(),
                        ListingImage::getImageKey
                ));

        return listings.stream()
                .map(listing -> {
                    String key = thumbnailKeys.get(listing.getId());
                    String thumbnailUrl = key == null ? null : buildUrl(key);
                    return toSummaryResponse(listing, thumbnailUrl);
                })
                .toList();
    }

    private void applyMarketPrice(Listing listing, Card card){
        if(card.getJustTcgId() == null){
            return;
        }

        BigDecimal marketPrice = cardPricingService.getCachedPrice(
                card.getJustTcgId(),
                listing.getCondition(),
                listing.getPrinting()
        );

        if(marketPrice == null){return;}

        listing.setMarketPrice(marketPrice);

        if(listing.getAskingPrice().compareTo(marketPrice.multiply(FLAG_MULTIPLIER)) > 0){
            listing.setPriceFlagged(true);
        }
    }



    private String buildUrl(String imageKey){
        return s3Properties.publicBaseUrl() + "/" + imageKey;
    }



    private ListingDetailsResponse toDetailResponse(Listing listing, List<String> imageUrls) {
        return new ListingDetailsResponse(
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
                listing.getSeller().getId(),
                imageUrls
        );
    }


    private ListingSummaryResponse toSummaryResponse(Listing listing, String thumbnailUrl){
        return new ListingSummaryResponse(
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
                listing.getSeller().getId(),
                thumbnailUrl
        );
    }
}
