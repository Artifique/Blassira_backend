package com.example.Blasira_Backend.dto.admin.support;

import com.example.Blasira_Backend.model.enums.TicketStatus;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {
    private TicketStatus status;
}
