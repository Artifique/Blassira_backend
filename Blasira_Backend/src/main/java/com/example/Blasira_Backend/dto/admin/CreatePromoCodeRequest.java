package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePromoCodeRequest {

    @NotBlank
    private String code;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @Min(0)
    private BigDecimal discountValue;

    @NotNull
    @Future
    private LocalDateTime expiresAt;

    @NotNull
    @Min(1)
    private int maxUses;
}
