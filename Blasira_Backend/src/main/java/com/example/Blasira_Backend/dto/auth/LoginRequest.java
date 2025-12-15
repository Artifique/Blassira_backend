package com.example.Blasira_Backend.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}
