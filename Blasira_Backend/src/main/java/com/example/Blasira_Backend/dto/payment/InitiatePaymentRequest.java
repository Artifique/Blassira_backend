package com.example.Blasira_Backend.dto.payment;

import com.example.Blasira_Backend.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiatePaymentRequest {
    @NotNull
    private Long bookingId;

    @NotNull
    private PaymentMethod paymentMethod;
}
