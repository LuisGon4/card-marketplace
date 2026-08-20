package com.spring.cardmarketplace.dto.tcgdex;

public record SetBrief(String id, String name, CardCount cardCount) {
    public record CardCount(int total, int official) {}
}
