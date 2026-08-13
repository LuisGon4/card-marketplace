package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.response.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public HealthResponse healthCheck(){
        return new HealthResponse("UP");
    }
}
