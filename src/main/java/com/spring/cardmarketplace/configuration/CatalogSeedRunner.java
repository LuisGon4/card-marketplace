package com.spring.cardmarketplace.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

@Profile("seed")
public class CatalogSeedRunner extends CommandLineRunner {
    private static final CatalogSeedService seedService;

    public CatalogSeedRunner(CatalogSeedService seedService) {
        this.seedService = seedService;
    }
    @Override
    public void run(String... args) throws Exception {
        seedService.seedCatalog();
    }
}
