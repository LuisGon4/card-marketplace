package com.spring.cardmarketplace.Repositories;

import com.spring.cardmarketplace.Entities.Card;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByCardNameContaining(String cardName);
}
