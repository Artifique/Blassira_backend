package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.admin.support.CreateMessageRequest;
import com.example.Blasira_Backend.dto.admin.support.MessageDto;
import com.example.Blasira_Backend.dto.admin.support.SupportTicketDto;
import com.example.Blasira_Backend.dto.admin.support.UpdateTicketStatusRequest;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.TicketStatus;
import com.example.Blasira_Backend.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/support/tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    /**
     * Récupère la liste paginée des tickets de support.
     * @param page Numéro de page (0-based).
     * @param limit Nombre d'éléments par page.
     * @param status Filtre par statut du ticket.
     * @param search Recherche par nom d'utilisateur ou sujet du ticket.
     * @return Une liste paginée de tickets de support.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("lastUpdatedAt").descending());
        Page<SupportTicketDto> ticketsPage = supportTicketService.getTickets(pageable, status, search);

        Map<String, Object> response = new HashMap<>();
        response.put("data", ticketsPage.getContent());
        Map<String, Object> paginationInfo = new HashMap<>();
        paginationInfo.put("currentPage", ticketsPage.getNumber());
        paginationInfo.put("totalPages", ticketsPage.getTotalPages());
        paginationInfo.put("totalItems", ticketsPage.getTotalElements());
        response.put("pagination", paginationInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les détails complets d'un ticket de support.
     * @param id L'ID du ticket.
     * @return Le DTO complet du ticket.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketDto> getTicketDetails(@PathVariable Long id) {
        return ResponseEntity.ok(supportTicketService.getTicketDetails(id));
    }

    /**
     * Ajoute une réponse à un ticket de support.
     * @param id L'ID du ticket.
     * @param request Le corps de la réponse.
     * @param currentUser L'administrateur authentifié qui répond.
     * @return Le DTO du message créé.
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<MessageDto> replyToTicket(
            @PathVariable Long id,
            @RequestBody CreateMessageRequest request,
            @AuthenticationPrincipal UserAccount currentUser) {
        MessageDto message = supportTicketService.replyToTicket(id, currentUser.getId(), request.getContent());
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    /**
     * Met à jour le statut d'un ticket de support.
     * @param id L'ID du ticket.
     * @param request Le nouveau statut.
     * @return Le DTO du ticket mis à jour.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<SupportTicketDto> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody UpdateTicketStatusRequest request) {
        SupportTicketDto updatedTicket = supportTicketService.updateTicketStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedTicket);
    }
}
