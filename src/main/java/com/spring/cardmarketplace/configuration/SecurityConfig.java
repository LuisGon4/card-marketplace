package com.spring.cardmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@Profile("!dev")
public class SecurityConfig {
    private static final String DEFAULT_SUCCESS_URL = "http://localhost:5173";

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
                        .requestMatchers("/api/dev/**").permitAll()
                        .requestMatchers("/ws").authenticated()
                        // Any other request must be authenticated
                        .anyRequest().authenticated())
                // OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true))
                .csrf(csrf -> {
                    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                    requestHandler.setCsrfRequestAttributeName(null);
                    csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .csrfTokenRequestHandler(requestHandler);

                });
        return http.build();
    }
}
