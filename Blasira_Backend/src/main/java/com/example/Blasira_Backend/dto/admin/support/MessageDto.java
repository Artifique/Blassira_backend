package com.example.Blasira_Backend.dto.admin.support;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private Long id;
    private SenderInfoDto sender; // Nested DTO for sender info
    private String content;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class SenderInfoDto {
        private Long id;
        private String name;
        private String role; // e.g., "user" or "admin"
    }
}
