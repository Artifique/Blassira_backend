package com.example.Blasira_Backend.dto.auth;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String phoneNumber;
    private String otp;
}
