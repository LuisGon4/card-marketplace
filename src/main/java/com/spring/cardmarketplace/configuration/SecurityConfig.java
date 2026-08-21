package com.spring.cardmarketplace.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@Profile("!dev & !seed")
public class SecurityConfig {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final OidcUserService customOidcUserService;

    public SecurityConfig(OidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        // Public reads
                        .requestMatchers(HttpMethod.GET, "/api/listings/**", "/api/cards/**", "/health").permitAll()
                        // Auth endpoints
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/ws").authenticated()
                        // Any other request must be authenticated
                        .anyRequest().authenticated())
                // OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .defaultSuccessUrl(frontendUrl, true))
                .csrf(csrf -> {
                        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                        requestHandler.setCsrfRequestAttributeName(null);

                        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                        repo.setCookieCustomizer(cookie ->
                                cookie.sameSite("Lax")
                                .secure(true));

                        csrf
                            .csrfTokenRepository(repo)
                            .csrfTokenRequestHandler(requestHandler);

                    }
                )
                .cors(Customizer.withDefaults());

        return http.build();
    }
}
