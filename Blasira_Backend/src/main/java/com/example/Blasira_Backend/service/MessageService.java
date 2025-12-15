package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.message.SendMessageRequest;
import com.example.Blasira_Backend.exception.UserProfileNotFoundException;
import com.example.Blasira_Backend.model.Conversation;
import com.example.Blasira_Backend.model.Message;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.repository.ConversationRepository;
import com.example.Blasira_Backend.repository.MessageRepository;
import com.example.Blasira_Backend.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public Message sendMessage(Long senderId, SendMessageRequest request) {
        UserProfile sender = userProfileRepository.findById(senderId)
                .orElseThrow(() -> new UserProfileNotFoundException("Sender not found"));
        UserProfile recipient = userProfileRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new UserProfileNotFoundException("Recipient not found"));

        Conversation conversation = conversationRepository.findConversationByParticipants(senderId, request.getRecipientId())
                .orElseGet(() -> createConversation(sender, recipient));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        boolean isParticipant = conversation.getParticipants().stream().anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new SecurityException("User is not a participant of this conversation");
        }

        return messageRepository.findByConversationOrderBySentAtAsc(conversation);
    }

    private Conversation createConversation(UserProfile user1, UserProfile user2) {
        Conversation newConversation = new Conversation();
        Set<UserProfile> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        newConversation.setParticipants(participants);
        return conversationRepository.save(newConversation);
    }
}
