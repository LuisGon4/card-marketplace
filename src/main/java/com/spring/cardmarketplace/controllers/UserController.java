package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.response.UserResponse;
import com.spring.cardmarketplace.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(){
       return userService.getCurrentUser();
    }
}
