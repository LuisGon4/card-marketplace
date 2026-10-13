package com.spring.cardmarketplace.dto.justtcg;

import java.util.List;

// card level: { "id": "...", "variants": [ ... ] }
public record JustTcgCard(
        String id,
        List<JustTcgVariant> variants
) {
}
