package com.spring.cardmarketplace.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private final String apiKey;
    private final String baseUrl;

    public RestClientConfig(
            @Value("${justtcg.api.key}") String apiKey,
            @Value("${justtcg.api.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Bean
    public RestClient justTcgRestClient(){
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key" , apiKey)
                .build();
    }
}
