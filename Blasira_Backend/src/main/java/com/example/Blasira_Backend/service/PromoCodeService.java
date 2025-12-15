package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.model.enums.DiscountType;
import com.example.Blasira_Backend.repository.PromoCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    @Transactional
    public PromoCode validateAndApplyPromoCode(String code, BigDecimal originalPrice) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code."));

        if (!promoCode.isActive() || promoCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Promo code is not active or has expired.");
        }

        if (promoCode.getUseCount() >= promoCode.getMaxUses()) {
            throw new IllegalArgumentException("Promo code has reached its maximum number of uses.");
        }

        // Le code promo est valide, incrémenter son compteur d'utilisation
        promoCode.setUseCount(promoCode.getUseCount() + 1);
        return promoCodeRepository.save(promoCode);
    }

    public BigDecimal calculateDiscountedPrice(PromoCode promoCode, BigDecimal originalPrice) {
        if (promoCode.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return originalPrice.subtract(promoCode.getDiscountValue());
        } else if (promoCode.getDiscountType() == DiscountType.PERCENTAGE) {
            BigDecimal discount = originalPrice.multiply(promoCode.getDiscountValue().divide(new BigDecimal(100)));
            return originalPrice.subtract(discount);
        }
        return originalPrice;
    }
}
