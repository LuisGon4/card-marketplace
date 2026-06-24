package com.spring.cardmarketplace.configuration;

import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.repositories.UserRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class FakeCurrentUserProvider implements CurrentUserProvider{
    private final UserRepository userRepository;

    @Setter
    private String currentUserEmail = "alice@example.com";

    public FakeCurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(currentUserEmail).
                orElseThrow(() -> new IllegalStateException("Seed data missing"));
    }

}
