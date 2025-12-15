package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.DiscountType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PromoCodeDto {
    private Long id;
    private String code;
    private DiscountType discountType;
    private double discountValue;
    private ZonedDateTime expiresAt;
    private int maxUses;
    private int useCount;
    private boolean active;
}
