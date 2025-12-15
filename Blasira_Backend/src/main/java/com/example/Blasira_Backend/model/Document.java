package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DocumentStatus; // NEW
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING) // Add this annotation for enum mapping
    @Column(nullable = false)
    private DocumentType documentType; // e.g., ID_CARD, INSTITUTIONAL_EMAIL, DRIVERS_LICENSE

    @Column(nullable = false)
    private String filePath; // URL or path to the stored document

    @Enumerated(EnumType.STRING) // NEW
    @Column(nullable = false)
    private DocumentStatus status; // PENDING, APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}