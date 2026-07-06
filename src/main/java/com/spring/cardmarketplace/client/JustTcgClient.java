package com.spring.cardmarketplace.client;


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
        try {
            JustTcgResponse response = justTcgRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cards")
                            .queryParam("cardId", justTcgId)
                            .queryParam("condition", condition.getApiValue())
                            .queryParam("printing", printing.getApiValue())
                            .build())
                    .retrieve()
                    .body(JustTcgResponse.class);

            if(response == null || response.data() == null || response.data().isEmpty() ||
                    response.data().getFirst().variants().isEmpty()){
                return null;
            }

            return response.data().getFirst().variants().getFirst().price();
        } catch(HttpClientErrorException.NotFound e){
            return null;
        }

    }
}
