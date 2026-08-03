package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.auth.SecurityContextCurrentUserProvider;
import com.spring.cardmarketplace.dto.response.UserResponse;
import com.spring.cardmarketplace.entities.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CurrentUserProvider currentUserProvider;

    public UserService(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public UserResponse getCurrentUser(){
        return toUserResponse(currentUserProvider.getCurrentUser());
    }

    private UserResponse toUserResponse(User user){
        return new UserResponse(user.getId(), user.getUsername());
    }
}
