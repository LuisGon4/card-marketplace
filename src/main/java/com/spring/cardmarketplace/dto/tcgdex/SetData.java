package com.spring.cardmarketplace.dto.tcgdex;

import java.util.List;

public record SetData(String id, String name, List<CardBrief> cards) {
    public record CardBrief(String id, String name, String image, String localId) {}
}