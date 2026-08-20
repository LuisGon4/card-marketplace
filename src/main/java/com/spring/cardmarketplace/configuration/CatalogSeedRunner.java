package com.spring.cardmarketplace.configuration;

import com.spring.cardmarketplace.services.CatalogSeedService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("seed")
@Component
public class CatalogSeedRunner implements CommandLineRunner {
    private final CatalogSeedService seedService;

    public CatalogSeedRunner(CatalogSeedService seedService) {
        this.seedService = seedService;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean success = seedService.seedCatalog();
        if (!success) {
            throw new IllegalStateException(
                    "Catalog seed failed: one or more sets did not seed");
        }
    }
}
