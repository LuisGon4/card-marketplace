package com.spring.cardmarketplace.dto.tcgdex;

import java.util.List;

public record RarityData(
        String rarityName,
        List<SetData.CardBrief> cards
) {
}
