package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminNotificationDto {
    private Long id;
    private Long senderAdminId;
    private String senderAdminName; // Display name of the admin
    private String title;
    private String content;
    private String type;
    private List<Long> recipientIds; // List of recipient IDs
    private LocalDateTime sentAt;
}
