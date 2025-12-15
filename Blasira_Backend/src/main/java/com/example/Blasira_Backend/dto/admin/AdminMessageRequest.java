package com.example.Blasira_Backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AdminMessageRequest {

    @NotEmpty
    private List<Long> recipientIds;

    @NotBlank
    private String content;
}
