package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.client.TcgDexClient;
import com.spring.cardmarketplace.dto.tcgdex.RarityData;
import com.spring.cardmarketplace.dto.tcgdex.SetBrief;
import com.spring.cardmarketplace.dto.tcgdex.SetData;
import com.spring.cardmarketplace.dto.tcgdex.SetResult;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> rarityMap = buildRarityMap();

        int setsFailed = 0;
        int setsSeeded = 0;
        int cardsUpserted = 0;
        int cardsFailed = 0;

        for(SetBrief brief : sets){
            try{
                SetData detail = tcgDexClient.fetchSetDetails(brief.id());
                SetResult r = setSeeder.setSeed(detail, rarityMap);
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

    private Map<String, String> buildRarityMap(){
        int fails = 0;

        Map<String, String> rarityMap = new HashMap<>();

        List<String> rarities = tcgDexClient.fetchAllRarities();

        for(String rarity : rarities){
            try {
                RarityData cardsOfRarity = tcgDexClient.fetchRarityCards(rarity);

                for (SetData.CardBrief cardBrief : cardsOfRarity.cards()) {
                    String prev = rarityMap.putIfAbsent(cardBrief.id(), rarity);
                    if (prev != null && !prev.equals(rarity)) {
                        log.warn("Duplicate rarity found for set {}, skipping", rarity);
                    }
                }
            }  catch (RestClientException e) {
                fails++;
            }
        }

        if(fails > 0){
            throw new IllegalStateException("Rarity map incomplete: fails=" + fails);
        }

        log.info("Rarity map built: rarities={} cards={}", rarities.size(), rarityMap.size());

        return rarityMap;
    }
}
