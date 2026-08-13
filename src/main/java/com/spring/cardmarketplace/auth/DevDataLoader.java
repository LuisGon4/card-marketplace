package com.spring.cardmarketplace.auth;

import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.repositories.CardRepository;
import com.spring.cardmarketplace.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataLoader {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public DevDataLoader(UserRepository userRepository, CardRepository cardRepository){
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @PostConstruct
    public void seedUsers() {
        if (userRepository.count() > 0){
            return;
        }

        User alice = new User();
        alice.setEmail("alice@example.com");
        alice.setDisplayName("Alice Test");
        alice.setUsername("alice_test");
        alice.setOauthProvider("google");
        alice.setOauthSubject("fake-oauth-subject-alice");
        userRepository.save(alice);

        User luis = new User();
        luis.setEmail("luis@example.com");
        luis.setDisplayName("Luis Test");
        luis.setUsername("luis_test");
        luis.setOauthProvider("google");
        luis.setOauthSubject("fake-oauth-subject-luis");
        userRepository.save(luis);
    }

    @PostConstruct
    public void seedCards() {

        if (cardRepository.count() == 1) {
            Card charizardEx = new Card();
            charizardEx.setCardName("Blastoise ex");
            charizardEx.setSetName("Scarlet & Violet 151");
            charizardEx.setRarity("Ultra Rare");
            charizardEx.setTcgDexId("sv03.5-184");
            charizardEx.setJustTcgId("pokemon-sv-scarlet-violet-151-blastoise-ex-184-165-ultra-rare");
            charizardEx.setImageUrl("https://assets.tcgdex.net/en/sv/sv03.5/184/high.webp");
            charizardEx.setCustom(true);

            cardRepository.save(charizardEx);
        }
    }
/*
    private void saveCard(String cardName, String setName, String rarity, String tcgDexId, String imageUrl) {
        Card card = new Card();
        card.setCardName(cardName);
        card.setSetName(setName);
        card.setRarity(rarity);
        card.setTcgDexId(tcgDexId);
        card.setImageUrl(imageUrl);
        card.setJustTcgId(null);
        card.setCustom(false);
        cardRepository.save(card);
    }

 */
}
