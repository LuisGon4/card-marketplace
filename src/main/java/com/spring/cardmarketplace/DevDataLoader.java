package com.spring.cardmarketplace;

import com.spring.cardmarketplace.Entities.Card;
import com.spring.cardmarketplace.Entities.User;
import com.spring.cardmarketplace.Repositories.CardRepository;
import com.spring.cardmarketplace.Repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
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
        alice.setOAuthProvider("google");
        alice.setOAuthSubject("fake-oauth-subject-alice");
        userRepository.save(alice);

        User luis = new User();
        luis.setEmail("luis@example.com");
        luis.setDisplayName("Luis Test");
        luis.setUsername("luis_test");
        luis.setOAuthProvider("google");
        luis.setOAuthSubject("fake-oauth-subject-luis");
        userRepository.save(luis);
    }

    @PostConstruct
    public void seedCards() {
        if (cardRepository.count() > 0) {
            return;
        }

        saveCard("Pikachu", "Base Set", "Common", "base1-58", "https://images.tcgdex.net/base1/58.png");
        saveCard("Charizard", "Base Set", "Rare Holo", "base1-4", "https://images.tcgdex.net/base1/4.png");
        saveCard("Blastoise", "Base Set", "Rare Holo", "base1-2", "https://images.tcgdex.net/base1/2.png");
        saveCard("Venusaur", "Base Set", "Rare Holo", "base1-15", "https://images.tcgdex.net/base1/15.png");
        saveCard("Mewtwo", "Base Set", "Rare Holo", "base1-10", "https://images.tcgdex.net/base1/10.png");
        saveCard("Gengar", "Fossil", "Rare Holo", "fossil-5", "https://images.tcgdex.net/fossil/5.png");
        saveCard("Furret", "Darkness Ablaze", "Common", "swsh3-136", "https://images.tcgdex.net/swsh3/136.png");
        saveCard("Eevee", "Jungle", "Common", "jungle-51", "https://images.tcgdex.net/jungle/51.png");
    }

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
}
