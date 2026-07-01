package com.spring.cardmarketplace.auth;

import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.UnauthenticatedException;
import com.spring.cardmarketplace.exception.UserNotFoundException;
import com.spring.cardmarketplace.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Profile("!dev")
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {
    private final UserRepository userRepository;

    public SecurityContextCurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated() || !(auth instanceof OAuth2AuthenticationToken oauthToken)){ // Not valid
            throw new UnauthenticatedException(
                    "User not authenticated"
            );
        }

        OAuth2User principal = oauthToken.getPrincipal();
        String sub = principal.getAttribute("sub");
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        return userRepository.findByOauthProviderAndOauthSubject(provider, sub)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
