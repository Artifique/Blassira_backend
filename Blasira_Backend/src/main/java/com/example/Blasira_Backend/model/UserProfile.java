package com.example.Blasira_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(columnDefinition = "longtext")
    private String bio;

    @Column(name = "member_since")
    private LocalDateTime memberSince;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude // Exclure pour éviter StackOverflowError
    @EqualsAndHashCode.Exclude // Exclure pour éviter StackOverflowError
    private UserAccount userAccount;

    @Column(name = "student_verified", nullable = false)
    private boolean studentVerified = false;
}
