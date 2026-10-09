package com.spring.cardmarketplace.dto.tcgdex;

// Set id, name, # of cards in set
public record SetBrief(String id, String name, CardCount cardCount) {
    public record CardCount(int total, int official) {}
}
