package com.spring.cardmarketplace.dto.tcgdex;

import java.util.List;

// Set id, name, cards in set
public record SetData(String id, String name, List<CardBrief> cards) {
    // Card id, name, image url, local id
    public record CardBrief(String id, String name, String image, String localId) {}
}