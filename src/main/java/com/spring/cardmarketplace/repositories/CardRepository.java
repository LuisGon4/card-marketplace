package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Card;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByCardNameContainingIgnoreCase(String cardName);

    List<Card> findByTcgDexIdIn(Collection<String> tcgDexIds);
}
