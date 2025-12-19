package com.example.Blasira_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admin_notifications")
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_admin_id", nullable = false)
    private UserAccount senderAdmin; // The admin who sent the notification

    @Column(nullable = false)
    private String title; // Subject of the notification

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // The message body

    @Column(nullable = false)
    private String type; // e.g., "BROADCAST", "INDIVIDUAL"

    @Column(name = "recipient_ids_json", columnDefinition = "TEXT")
    private String recipientIdsJson; // JSON array of recipient UserAccount IDs

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
}
