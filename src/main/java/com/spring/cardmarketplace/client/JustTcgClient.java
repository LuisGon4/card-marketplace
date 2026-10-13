package com.spring.cardmarketplace.client;


import com.spring.cardmarketplace.dto.justtcg.JustTcgCard;
import com.spring.cardmarketplace.dto.justtcg.JustTcgPrice;
import com.spring.cardmarketplace.dto.justtcg.JustTcgResponse;
import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class JustTcgClient {
    private final RestClient justTcgRestClient;

    public JustTcgClient(@Qualifier("justTcgRestClient") RestClient justTcgRestClient) {
        this.justTcgRestClient = justTcgRestClient;
    }

    public BigDecimal fetchPrice(String justTcgId, Condition condition, Printing printing){
        JustTcgPrice result = fetch("cardId", justTcgId, condition, printing);
        return result == null ? null : result.price();
    }

    // Lookup by TCGPlayer product id; also returns the JustTCG card id so it can be stored
    public JustTcgPrice fetchByTcgPlayerId(String tcgPlayerId, Condition condition, Printing printing){
        return fetch("tcgplayerId", tcgPlayerId, condition, printing);
    }

    // Returns null when the card/variant doesn't exist; other errors (429, 401, 5xx) propagate
    private JustTcgPrice fetch(String param, String value, Condition condition, Printing printing){
        try {
            JustTcgResponse response = justTcgRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cards")
                            .queryParam(param, value)
                            .queryParam("condition", condition.getApiValue())
                            .queryParam("printing", printing.getApiValue())
                            .build())
                    .retrieve()
                    .body(JustTcgResponse.class);

            if(response == null || response.data() == null || response.data().isEmpty()){
                return null;
            }

            JustTcgCard card = response.data().getFirst();
            if(card.variants() == null || card.variants().isEmpty()){
                return null;
            }

            return new JustTcgPrice(card.id(), card.variants().getFirst().price());
        } catch(HttpClientErrorException.NotFound e){
            return null;
        }
    }
}
