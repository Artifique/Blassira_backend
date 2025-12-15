package com.example.Blasira_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "conversation_participants",
        joinColumns = @JoinColumn(name = "conversation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_profile_id")
    )
    @ToString.Exclude // Exclure pour éviter StackOverflowError
    @EqualsAndHashCode.Exclude // Exclure pour éviter StackOverflowError
    private Set<UserProfile> participants = new HashSet<>();
}
