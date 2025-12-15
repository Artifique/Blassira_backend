package com.example.Blasira_Backend.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    private Long id;
    private Long authorId;
    private String authorFirstName;
    private Long recipientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
