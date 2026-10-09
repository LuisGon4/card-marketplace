package com.spring.cardmarketplace.dto.tcgdex;

import java.util.List;

public record RarityData(
        String name,
        List<SetData.CardBrief> cards
) {
}
