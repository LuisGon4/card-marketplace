package com.spring.cardmarketplace.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private final String apiKey;
    private final String justTcgBaseUrl;
    private final String tcgDexBaseUrl;

    public RestClientConfig(
            @Value("${justtcg.api.key}") String apiKey,
            @Value("${justtcg.api.base-url}") String justTcgBaseUrl,
            @Value("${tcgdex.api.base-url}") String tcgDexBaseUrl) {
        this.apiKey = apiKey;
        this.justTcgBaseUrl = justTcgBaseUrl;
        this.tcgDexBaseUrl = tcgDexBaseUrl;
    }

    @Bean
    public RestClient justTcgRestClient(){
        return RestClient.builder()
                .baseUrl(justTcgBaseUrl)
                .defaultHeader("x-api-key" , apiKey)
                .build();
    }

    @Bean
    public RestClient tcgDexClient(){
        return RestClient.builder()
                .baseUrl(tcgDexBaseUrl)
                .build();
    }
}
