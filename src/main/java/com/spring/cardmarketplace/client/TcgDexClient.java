package com.spring.cardmarketplace.client;

import com.spring.cardmarketplace.dto.tcgdex.SetBrief;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Profile("seed")
@Component
public class TcgDexClient {
    private final RestClient tcgDexClient;

    public TcgDexClient(@Qualifier("tcgDexClient") RestClient tcgDexClient) {
        this.tcgDexClient = tcgDexClient;
    }

    public List<SetBrief> fetchAllSets(){
        return tcgDexClient.get()
                .uri("/sets")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SetBrief>>() {});
    }
}
