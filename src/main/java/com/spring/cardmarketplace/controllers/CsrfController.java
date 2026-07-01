package com.spring.cardmarketplace.controllers;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CsrfController {
    @GetMapping("/api/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;   // injecting CsrfToken forces it to resolve → cookie gets written
    }
}
