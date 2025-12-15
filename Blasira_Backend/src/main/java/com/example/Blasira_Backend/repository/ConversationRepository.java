package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id IN (:user1Id, :user2Id) GROUP BY c.id HAVING COUNT(DISTINCT p.id) = 2")
    Optional<Conversation> findConversationByParticipants(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
