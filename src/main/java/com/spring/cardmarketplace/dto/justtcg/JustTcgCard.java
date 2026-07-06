package com.spring.cardmarketplace.dto.justtcg;

import java.util.List;

// card level: { "variants": [ ... ] }
public record JustTcgCard(
        List<JustTcgVariant> variants
) {
}
