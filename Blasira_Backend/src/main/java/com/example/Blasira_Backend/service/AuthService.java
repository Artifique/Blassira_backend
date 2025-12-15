package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.auth.JwtAuthenticationResponse;
import com.example.Blasira_Backend.dto.auth.LoginRequest;
import com.example.Blasira_Backend.dto.auth.SignUpRequest;
import com.example.Blasira_Backend.model.UserAccount;

public interface AuthService {
    JwtAuthenticationResponse signup(SignUpRequest signUpRequest);
    JwtAuthenticationResponse login(LoginRequest loginRequest);
}
