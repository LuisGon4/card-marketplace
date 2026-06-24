package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.CreateListingRequest;
import com.spring.cardmarketplace.dto.request.ListingFilter;
import com.spring.cardmarketplace.dto.request.UpdateListingRequest;
import com.spring.cardmarketplace.dto.response.ListingResponse;
import com.spring.cardmarketplace.services.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/listings")
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService){
        this.listingService = listingService;
    }

    @GetMapping
    public List<ListingResponse> getListings(ListingFilter filter){
        return listingService.findAll(filter);
    }

    @GetMapping("/{id}")
    public ListingResponse getListing(@PathVariable UUID id){
        return listingService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@Valid @RequestBody CreateListingRequest request){
        return listingService.create(request);
    }

    @PatchMapping("/{id}")
    public ListingResponse updateListing(@PathVariable UUID id, @RequestBody UpdateListingRequest request){
        return listingService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteListing(@PathVariable UUID id){
        listingService.delete(id);
    }
}
