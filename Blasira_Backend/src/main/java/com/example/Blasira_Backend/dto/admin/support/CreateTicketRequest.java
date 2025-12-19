package com.example.Blasira_Backend.dto.admin.support;

import com.example.Blasira_Backend.model.enums.TicketCategory;
import com.example.Blasira_Backend.model.enums.TicketPriority;
import lombok.Data;

@Data
public class CreateTicketRequest {
    private String subject;
    private TicketCategory category;
    private TicketPriority priority;
    private String initialMessage; // The first message from the user
}
