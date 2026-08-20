package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.dto.tcgdex.SetBrief;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile("seed")
@Service
public class CatalogSeedService {
    private final CardRepository cardRepository;

    public CatalogSeedService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void seedCatalog() {
        List<SetBrief> sets = tcgDexClient.fetchAllSets();
        int setsFailed = 0;
        int setsSeeded = 0;

        for(SetBrief brief : sets){
            try{
                SetData detail = tcgDexClient.fetchSetDetails(brief.id());
                setSeeder.setSeed(detail);
                setsSeeded++;
            } catch(Exception e){
                setsFailed++;
            }
        }
    }
}
