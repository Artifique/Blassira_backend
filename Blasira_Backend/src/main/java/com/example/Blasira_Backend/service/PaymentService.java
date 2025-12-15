package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.payment.InitiatePaymentResponse;
import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.Payment;
import com.example.Blasira_Backend.model.enums.BookingStatus;
import com.example.Blasira_Backend.model.enums.PaymentMethod;
import com.example.Blasira_Backend.model.enums.PaymentStatus;
import com.example.Blasira_Backend.repository.BookingRepository;
import com.example.Blasira_Backend.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public InitiatePaymentResponse initiatePayment(Long bookingId, Long userId, PaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Valider que l'utilisateur initiant le paiement est le passager
        if (!booking.getPassenger().getId().equals(userId)) {
            throw new SecurityException("User is not authorized to pay for this booking.");
        }

        // Empêcher le paiement d'une réservation qui n'est pas confirmée ou déjà payée
        if (booking.getStatus() != BookingStatus.CONFIRMED_BY_DRIVER) {
            throw new IllegalStateException("Booking is not in a state that allows payment.");
        }

        // Créer et sauvegarder l'enregistrement de paiement
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(UUID.randomUUID().toString()); // Référence de transaction externe
        paymentRepository.save(payment);

        // --- INTERACTION SIMULÉE AVEC LA PASSERELLE DE PAIEMENT ---
        log.info("Initiating payment for booking {}. Amount: {}. Method: {}. Transaction ID: {}",
                booking.getId(), payment.getAmount(), paymentMethod, payment.getTransactionId());

        // Dans un scénario réel, vous appelleriez l'API de la passerelle de paiement ici
        // et obtiendriez une vraie URL de paiement.
        String fakePaymentGatewayUrl = "https://your-payment-gateway.com/pay/" + payment.getTransactionId();

        return InitiatePaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentGatewayUrl(fakePaymentGatewayUrl)
                .transactionReference(payment.getTransactionId())
                .build();
    }

    @Transactional
    public void handlePaymentWebhook(String transactionId, boolean success) {
        log.info("Received payment webhook for transaction {}. Success: {}", transactionId, success);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment with transaction ID " + transactionId + " not found."));

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESSFUL);
            Booking booking = payment.getBooking();
            // Vous pourriez vouloir un statut plus spécifique comme PAID
            booking.setStatus(BookingStatus.CONFIRMED_BY_DRIVER); // Ou un nouveau statut
            bookingRepository.save(booking);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
    }
}
