package com.spring.cardmarketplace.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity @Getter @Setter
@Table(name = "listing_images")
public class ListingImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false) // Many images can go in one listing
    private Listing listing;

    @Column(name = "image_key", nullable = false, length = 512)
    private String imageKey;

    @Column(nullable = false)
    private int position;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
