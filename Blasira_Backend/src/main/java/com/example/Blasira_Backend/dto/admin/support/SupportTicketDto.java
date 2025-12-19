package com.example.Blasira_Backend.dto.admin.support;

import com.example.Blasira_Backend.model.enums.TicketCategory;
import com.example.Blasira_Backend.model.enums.TicketPriority;
import com.example.Blasira_Backend.model.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SupportTicketDto {
    private Long id;
    private String subject;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private UserInfoDto user; // Nested DTO for user info
    private List<MessageDto> messages; // Full message history

    @Data
    @Builder
    public static class UserInfoDto {
        private Long id;
        private String name;
        private String email;
    }
}
