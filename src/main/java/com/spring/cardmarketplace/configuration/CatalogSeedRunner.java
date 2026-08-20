package com.spring.cardmarketplace.configuration;

import com.spring.cardmarketplace.services.CatalogSeedService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

@Profile("seed")
public class CatalogSeedRunner implements CommandLineRunner {
    private final CatalogSeedService seedService;

    public CatalogSeedRunner(CatalogSeedService seedService) {
        this.seedService = seedService;
    }
    @Override
    public void run(String... args) throws Exception {
        seedService.seedCatalog();
    }
}
