package com.spring.cardmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    private final OidcUserService customOidcUserService;

    public SecurityConfig(OidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        // Public reads
                        .requestMatchers(HttpMethod.GET, "/api/listings/**", "/api/cards/**").permitAll()
                        // Auth endpoints
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        // Dev endpoints
                        .requestMatchers("/api/dev/***").permitAll()
                        // Any other request must be authenticated
                        .anyRequest().authenticated())
                // OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }
}
