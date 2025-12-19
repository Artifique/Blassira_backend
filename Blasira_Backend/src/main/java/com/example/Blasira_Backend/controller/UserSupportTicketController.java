package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.admin.support.CreateTicketRequest;
import com.example.Blasira_Backend.dto.admin.support.SupportTicketDto;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/support/tickets")
@RequiredArgsConstructor
public class UserSupportTicketController {

    private final SupportTicketService supportTicketService;

    /**
     * Permet à un utilisateur de créer un nouveau ticket de support.
     * @param request Le DTO contenant les détails du ticket à créer.
     * @param currentUser L'utilisateur authentifié qui crée le ticket.
     * @return Le DTO du ticket de support créé.
     */
    @PostMapping
    public ResponseEntity<SupportTicketDto> createTicket(
            @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal UserAccount currentUser) {
        SupportTicketDto createdTicket = supportTicketService.createTicket(currentUser.getId(), request);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
}
