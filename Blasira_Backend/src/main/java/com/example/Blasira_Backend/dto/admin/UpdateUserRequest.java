package com.example.Blasira_Backend.dto.admin;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password; // Optional, only update if provided
}
