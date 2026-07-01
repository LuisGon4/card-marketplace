package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.repositories.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        OidcUser oidcUser = super.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();
        String sub   = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name  = oidcUser.getFullName();

        String safeName = (name != null) ? name : email;

        userRepository.findByOauthProviderAndOauthSubject(provider, sub)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setOauthProvider(provider);
                    newUser.setOauthSubject(sub);
                    newUser.setDisplayName(safeName);
                    newUser.setUsername(generateUniqueUsername(safeName));
                    return userRepository.save(newUser);
                });

        return oidcUser;
    }

    private String generateUniqueUsername(String displayName){
        String base = sanitizeInput(displayName);
        String candidate = base;
        int counter = 1;

        while(userRepository.existsByUsername(candidate)){
            candidate = base + counter;
            counter++;
        }

        return candidate;
    }

    private String sanitizeInput(String input){
        StringBuilder sb = new StringBuilder(input.length());

        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            if(Character.isLetterOrDigit(c)){
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.isEmpty() ? "user" : sb.toString();
    }
}
