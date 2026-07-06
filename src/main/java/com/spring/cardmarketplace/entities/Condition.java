package com.spring.cardmarketplace.entities;


import lombok.Getter;

@Getter
public enum Condition {
    NM("Near Mint"), LP("Lightly Played"), MP("Moderately Played"),
    HP("Heavily Played"), DMG("Damaged");

    private final String apiValue;

    Condition(String apiValue) { this.apiValue = apiValue; }
}

