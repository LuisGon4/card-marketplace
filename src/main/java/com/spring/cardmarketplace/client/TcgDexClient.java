package com.spring.cardmarketplace.client;

import com.spring.cardmarketplace.dto.tcgdex.RarityData;
import com.spring.cardmarketplace.dto.tcgdex.SetBrief;
import com.spring.cardmarketplace.dto.tcgdex.SetData;
import com.spring.cardmarketplace.dto.tcgdex.TcgDexCard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TcgDexClient {
    private final RestClient tcgDexRestClient;

    public TcgDexClient(@Qualifier("tcgDexRestClient") RestClient tcgDexRestClient) {
        this.tcgDexRestClient = tcgDexRestClient;
    }

    public List<SetBrief> fetchAllSets(){
        return tcgDexRestClient.get()
                .uri("/sets")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SetBrief>>() {});
    }

    public SetData fetchSetDetails(String setId){
        return tcgDexRestClient.get()
                .uri("/sets/{id}", setId)
                .retrieve()
                .body(SetData.class);
    }

    public List<String> fetchAllRarities() {
        return tcgDexRestClient.get()
                .uri("/rarities")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }


    public RarityData fetchRarityCards(String rarity){
        return tcgDexRestClient.get()
                .uri("/rarities/{rarity}", rarity)
                .retrieve()
                .body(RarityData.class);
    }

    // Card detail incl. TCGPlayer product ids; null if the card doesn't exist
    public TcgDexCard fetchCard(String cardId){
        try {
            return tcgDexRestClient.get()
                    .uri("/cards/{id}", cardId)
                    .retrieve()
                    .body(TcgDexCard.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
