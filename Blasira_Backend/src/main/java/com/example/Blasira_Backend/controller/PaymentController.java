package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.payment.InitiatePaymentRequest;
import com.example.Blasira_Backend.dto.payment.InitiatePaymentResponse;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserAccount currentUser = (UserAccount) userDetails;

        InitiatePaymentResponse response = paymentService.initiatePayment(
                request.getBookingId(),
                currentUser.getId(),
                request.getPaymentMethod()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Webhook pour que les passerelles de paiement nous notifient du statut du paiement.
     * Ce endpoint devrait être publiquement accessible mais sécurisé.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        // Dans une application réelle, vous devriez valider la signature du webhook ici
        String transactionId = (String) payload.get("transactionId");
        boolean success = (boolean) payload.get("success");

        paymentService.handlePaymentWebhook(transactionId, success);

        return ResponseEntity.ok().build();
    }
}
