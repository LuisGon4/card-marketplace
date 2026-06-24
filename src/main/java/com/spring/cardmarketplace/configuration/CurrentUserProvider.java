package com.spring.cardmarketplace.configuration;

import com.spring.cardmarketplace.entities.User;

public interface CurrentUserProvider {
    User getCurrentUser();
}
