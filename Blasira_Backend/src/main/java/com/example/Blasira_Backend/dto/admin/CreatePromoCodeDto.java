package com.example.Blasira_Backend.dto.admin;

import java.time.ZonedDateTime;

public class CreatePromoCodeDto {
    private String code;
    private String discountType; // Peut être "PERCENTAGE" ou "FIXED_AMOUNT"
    private double discountValue;
    private ZonedDateTime expirationDate;
    private int maxUses;
    private int usesPerUser;
    private boolean isActive;

    // Constructeur par défaut
    public CreatePromoCodeDto() {
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ZonedDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int getUsesPerUser() {
        return usesPerUser;
    }

    public void setUsesPerUser(int usesPerUser) {
        this.usesPerUser = usesPerUser;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
