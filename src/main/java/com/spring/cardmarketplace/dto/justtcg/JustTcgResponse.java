package com.spring.cardmarketplace.dto.justtcg;

import java.util.List;

// outer wrapper: { "data": [ ... ] }
public record JustTcgResponse(
        List<JustTcgCard> data
) {
}
