package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.support.CreateTicketRequest;
import com.example.Blasira_Backend.dto.admin.support.MessageDto;
import com.example.Blasira_Backend.dto.admin.support.SupportTicketDto;
import com.example.Blasira_Backend.model.SupportTicket;
import com.example.Blasira_Backend.model.SupportTicketMessage;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.TicketStatus;
import com.example.Blasira_Backend.repository.SupportTicketMessageRepository;
import com.example.Blasira_Backend.repository.SupportTicketRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime; // NEW
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketMessageRepository supportTicketMessageRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public SupportTicketDto createTicket(Long userId, CreateTicketRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN); // Default status

        // Create initial message
        SupportTicketMessage initialMessage = new SupportTicketMessage();
        initialMessage.setTicket(ticket);
        initialMessage.setSender(user);
        initialMessage.setContent(request.getInitialMessage());
        
        ticket = supportTicketRepository.save(ticket); // Save ticket first to get ID
        initialMessage.setTicket(ticket); // Set ticket on message
        supportTicketMessageRepository.save(initialMessage); // Save message

        // Attach message to ticket (optional, as it's OneToMany mapped)
        ticket.setMessages(List.of(initialMessage));

        return mapToSupportTicketDto(ticket);
    }

    @Transactional(readOnly = true)
    public Page<SupportTicketDto> getTickets(Pageable pageable, TicketStatus status, String search) {
        Page<SupportTicket> ticketsPage = supportTicketRepository.findByStatusAndSearch(status, search, pageable);
        return ticketsPage.map(this::mapToSupportTicketDtoList);
    }

    @Transactional(readOnly = true)
    public SupportTicketDto getTicketDetails(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Support Ticket not found with ID: " + ticketId));
        return mapToSupportTicketDto(ticket);
    }

    @Transactional
    public MessageDto replyToTicket(Long ticketId, Long senderId, String content) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Support Ticket not found with ID: " + ticketId));
        UserAccount sender = userAccountRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with ID: " + senderId));

        SupportTicketMessage message = new SupportTicketMessage();
        message.setTicket(ticket);
        message.setSender(sender);
        message.setContent(content);
        supportTicketMessageRepository.save(message);

        ticket.setLastUpdatedAt(LocalDateTime.now()); // Update last updated timestamp
        ticket.setStatus(TicketStatus.PENDING); // Set to pending when replied
        supportTicketRepository.save(ticket);

        return mapToMessageDto(message);
    }

    @Transactional
    public SupportTicketDto updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Support Ticket not found with ID: " + ticketId));
        ticket.setStatus(newStatus);
        ticket.setLastUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
        return mapToSupportTicketDto(ticket);
    }

    // --- Mappers ---
    private SupportTicketDto mapToSupportTicketDto(SupportTicket ticket) {
        return SupportTicketDto.builder()
                .id(ticket.getId())
                .subject(ticket.getSubject())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .lastUpdatedAt(ticket.getLastUpdatedAt())
                .user(SupportTicketDto.UserInfoDto.builder()
                        .id(ticket.getUser().getId())
                        .name(ticket.getUser().getUserProfile() != null ? ticket.getUser().getUserProfile().getFirstName() + " " + ticket.getUser().getUserProfile().getLastName() : ticket.getUser().getEmail())
                        .email(ticket.getUser().getEmail())
                        .build())
                .messages(ticket.getMessages().stream()
                        .map(this::mapToMessageDto)
                        .collect(Collectors.toList()))
                .build();
    }

    // Mapper for list view (only last message or summarized)
    private SupportTicketDto mapToSupportTicketDtoList(SupportTicket ticket) {
        // For list view, we might only need basic info and not all messages
        return SupportTicketDto.builder()
                .id(ticket.getId())
                .subject(ticket.getSubject())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .lastUpdatedAt(ticket.getLastUpdatedAt())
                .user(SupportTicketDto.UserInfoDto.builder()
                        .id(ticket.getUser().getId())
                        .name(ticket.getUser().getUserProfile() != null ? ticket.getUser().getUserProfile().getFirstName() + " " + ticket.getUser().getUserProfile().getLastName() : ticket.getUser().getEmail())
                        .build())
                .build();
    }

    private MessageDto mapToMessageDto(SupportTicketMessage message) {
        String senderRole = "user"; // Default to user
        if (message.getSender().getRoles().contains(com.example.Blasira_Backend.model.enums.Role.ROLE_ADMIN)) {
            senderRole = "admin";
        }
        return MessageDto.builder()
                .id(message.getId())
                .sender(MessageDto.SenderInfoDto.builder()
                        .id(message.getSender().getId())
                        .name(message.getSender().getUserProfile() != null ? message.getSender().getUserProfile().getFirstName() : message.getSender().getEmail())
                        .role(senderRole)
                        .build())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
