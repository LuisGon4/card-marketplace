package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.client.TcgDexClient;
import com.spring.cardmarketplace.dto.tcgdex.SetBrief;
import com.spring.cardmarketplace.dto.tcgdex.SetData;
import com.spring.cardmarketplace.dto.tcgdex.SetResult;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile("seed")
@Service
public class CatalogSeedService {
    private static final Logger log = LoggerFactory.getLogger(CatalogSeedService.class);

    private final TcgDexClient tcgDexClient;
    private final SetSeeder setSeeder;

    public CatalogSeedService(TcgDexClient tcgDexClient, SetSeeder setSeeder) {
        this.tcgDexClient = tcgDexClient;
        this.setSeeder = setSeeder;
    }

    public boolean seedCatalog() {
        List<SetBrief> sets = tcgDexClient.fetchAllSets();
        int setsFailed = 0;
        int setsSeeded = 0;
        int cardsUpserted = 0;
        int cardsFailed = 0;

        for(SetBrief brief : sets){
            try{
                SetData detail = tcgDexClient.fetchSetDetails(brief.id());
                SetResult r = setSeeder.setSeed(detail);
                cardsUpserted += r.upserted();
                cardsFailed += r.failed();
                setsSeeded++;
            } catch (Exception e) {
                log.warn("Failed to seed set {}, skipping", brief.id(), e);
                setsFailed++;
            }
        }

        log.info("Seed complete: setsSeeded={} setsFailed={} cardsUpserted={} cardsFailed={}",
                setsSeeded, setsFailed, cardsUpserted, cardsFailed);

        return setsFailed == 0;
    }
}
