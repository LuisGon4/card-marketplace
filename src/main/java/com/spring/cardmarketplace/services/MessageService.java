package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.auth.CurrentUserProvider;
import com.spring.cardmarketplace.dto.response.MessageResponse;
import com.spring.cardmarketplace.entities.Conversation;
import com.spring.cardmarketplace.entities.Message;
import com.spring.cardmarketplace.entities.User;
import com.spring.cardmarketplace.exception.ConversationNotFoundException;
import com.spring.cardmarketplace.exception.ForbiddenOperationException;
import com.spring.cardmarketplace.repositories.ConversationRepository;
import com.spring.cardmarketplace.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final CurrentUserProvider currentUserProvider;

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository,  CurrentUserProvider currentUserProvider) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public MessageResponse sendMessage(UUID conversationId, String content) {
        User currentUser = currentUserProvider.getCurrentUser();
        Conversation conversation = getConversationAsParticipant(conversationId,  currentUser);


        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setContent(content);

        messageRepository.saveAndFlush(message);

        return toResponse(message);
    }

    public List<MessageResponse> getMessages(UUID conversationId) {
        User currentUser = currentUserProvider.getCurrentUser();
        Conversation conversation = getConversationAsParticipant(conversationId, currentUser);

        return messageRepository.findByConversationOrderBySentAtAsc(conversation)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Conversation getConversationAsParticipant(UUID conversationId, User user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "No conversation found with id: " + conversationId
                ));

        if(!user.getId().equals(conversation.getBuyer().getId()) && !user.getId().equals(conversation.getSeller().getId())){
            throw new ForbiddenOperationException("You are not allowed to send messages");
        }

        return conversation;
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getSentAt()
        );
    }
}