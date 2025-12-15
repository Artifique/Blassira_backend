package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.message.MessageDto;
import com.example.Blasira_Backend.dto.message.SendMessageRequest;
import com.example.Blasira_Backend.model.Message;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal UserAccount currentUser) {

        Message message = messageService.sendMessage(currentUser.getId(), request);
        return new ResponseEntity<>(toDto(message), HttpStatus.CREATED);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getConversationMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal UserAccount currentUser) {

        List<Message> messages = messageService.getConversationMessages(conversationId, currentUser.getId());
        List<MessageDto> messageDtos = messages.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(messageDtos);
    }

    private MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderFirstName(message.getSender().getFirstName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}
