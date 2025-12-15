package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.CreatePromoCodeRequest;
import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeManagementService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCode createPromoCode(CreatePromoCodeRequest request) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(request.getCode().toUpperCase());
        promoCode.setDiscountType(request.getDiscountType());
        promoCode.setDiscountValue(request.getDiscountValue());
        promoCode.setExpiresAt(request.getExpiresAt());
        promoCode.setMaxUses(request.getMaxUses());
        return promoCodeRepository.save(promoCode);
    }

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }
}
