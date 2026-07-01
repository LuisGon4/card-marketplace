package com.spring.cardmarketplace.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Many (Current class) to One (Data Field Class)
    @ManyToOne(fetch = FetchType.LAZY) // Many listings belong to one seller
    @JoinColumn(name = "seller_id", nullable = false) // Foreign key w/ col name "seller_id"
    private User seller; // listings.seller_id -> user.id

    // Only need from this direction because we can find all Listings with a certain
    // card using: listingRepository.findAll(hasCardName("Charizard"));
    // Don't need @OneToMany in card because we never need card.getListings(), Keep it unidirectional
    @ManyToOne(fetch = FetchType.LAZY) // Many listings can reference the same card
    @JoinColumn(name = "card_id", nullable = false)
    private Card card; // listings.card -> cards.id

    @Column(nullable = false, length = 50)
    private String printing;

    @Column(nullable = false, length = 20)
    private String condition; // "DMG", "HP", "MP", "LP", "NM", "M"

    @Column(name = "asking_price", nullable = false)
    private BigDecimal askingPrice;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "is_price_flagged")
    private boolean priceFlagged ;

    @Column(nullable = false, length = 250)
    private String location;

    @Column(length = 250)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
