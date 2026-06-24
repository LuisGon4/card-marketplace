package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Conversation;
import com.spring.cardmarketplace.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
}
