package com.example.Blasira_Backend.dto.message;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderFirstName;
    private String content;
    private LocalDateTime sentAt;
}
