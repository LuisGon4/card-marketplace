package com.spring.cardmarketplace.Repositories;

import com.spring.cardmarketplace.Entities.Conversation;
import com.spring.cardmarketplace.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
}
