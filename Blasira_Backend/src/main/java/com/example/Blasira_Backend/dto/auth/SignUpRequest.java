package com.example.Blasira_Backend.dto.auth;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

    @AssertTrue(message = "The Trust Charter must be accepted.")
    private boolean acceptedTrustCharter;
}
