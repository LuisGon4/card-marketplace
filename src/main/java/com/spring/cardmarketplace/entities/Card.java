package com.spring.cardmarketplace.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    @Column(name = "set_name")
    private String setName;

    @Column(length = 50)
    private String rarity;

    @Column(name = "just_tcg_id")
    private String justTcgId;

    @Column(name = "tcgdex_id", unique = true)
    private String tcgDexId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false) // Either custom card or in database
    private boolean custom;
}
