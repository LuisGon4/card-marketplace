package com.spring.cardmarketplace.entities;

import lombok.Getter;

@Getter
public enum Printing {
    NORMAL("Normal"), HOLOFOIL("Holofoil"),
    REVERSE_HOLOFOIL("Reverse Holofoil");
    private final String apiValue;
    Printing(String apiValue) { this.apiValue = apiValue; }
}
