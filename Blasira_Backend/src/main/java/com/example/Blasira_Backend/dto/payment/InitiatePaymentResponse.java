package com.example.Blasira_Backend.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiatePaymentResponse {
    private Long paymentId;
    private String paymentGatewayUrl; // URL to redirect the user to
    private String transactionReference;
}
