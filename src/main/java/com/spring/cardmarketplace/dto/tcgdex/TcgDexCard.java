package com.spring.cardmarketplace.dto.tcgdex;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.cardmarketplace.entities.Printing;

import java.util.stream.Stream;

// Card detail: { "id": ..., "pricing": { "tcgplayer": { "<printing>": { "productId": ... } } } }
public record TcgDexCard(String id, Pricing pricing) {
    public record Pricing(TcgPlayer tcgplayer) {}

    public record TcgPlayer(
            Price normal,
            Price holofoil,
            @JsonProperty("reverse-holofoil") Price reverseHolofoil
    ) {}

    public record Price(Long productId) {}

    // TCGPlayer product id (== JustTCG tcgplayerId); prefers the requested printing, falls back to any
    public String productIdFor(Printing printing) {
        if (pricing == null || pricing.tcgplayer() == null) {
            return null;
        }

        TcgPlayer t = pricing.tcgplayer();
        Price preferred = switch (printing) {
            case NORMAL -> t.normal();
            case HOLOFOIL -> t.holofoil();
            case REVERSE_HOLOFOIL -> t.reverseHolofoil();
        };

        return Stream.of(preferred, t.normal(), t.holofoil(), t.reverseHolofoil())
                .filter(p -> p != null && p.productId() != null)
                .map(p -> String.valueOf(p.productId()))
                .findFirst()
                .orElse(null);
    }
}
