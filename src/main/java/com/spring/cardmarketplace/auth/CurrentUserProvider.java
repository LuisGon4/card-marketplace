package com.spring.cardmarketplace.auth;

import com.spring.cardmarketplace.entities.User;

public interface CurrentUserProvider {
    User getCurrentUser();
}
