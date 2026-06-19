package com.spring.cardmarketplace.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table(name = "conversations",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"listing_id", "buyer_id", "seller_id"}))
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne // Many conversations can have this sender
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne // Many conversations can have this recipient
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
