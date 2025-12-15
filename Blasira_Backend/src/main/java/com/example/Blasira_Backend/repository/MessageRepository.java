package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Conversation;
import com.example.Blasira_Backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
}
