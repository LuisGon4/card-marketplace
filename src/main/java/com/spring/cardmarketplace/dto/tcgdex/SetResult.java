package com.spring.cardmarketplace.dto.tcgdex;

public record SetResult(
        int upserted,
        int failed
) {
}
