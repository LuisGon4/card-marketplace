package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.configuration.FakeCurrentUserProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Profile("dev")
public class DevController {
    private final FakeCurrentUserProvider fakeCurrentUserProvider;

    public DevController(FakeCurrentUserProvider fakeCurrentUserProvider) {
        this.fakeCurrentUserProvider = fakeCurrentUserProvider;
    }

    @PostMapping("/switch-user")
    public void switchUser(@RequestParam String email){
        fakeCurrentUserProvider.setCurrentUserEmail(email);
    }
}
