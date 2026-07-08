package com.spring.cardmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Profile("!dev")
@EnableWebSocketSecurity
@Configuration
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**").authenticated()
                .anyMessage().denyAll();
        return messages.build();
    }
}
