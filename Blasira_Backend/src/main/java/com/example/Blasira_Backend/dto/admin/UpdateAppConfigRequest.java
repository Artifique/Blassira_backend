package com.example.Blasira_Backend.dto.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAppConfigRequest {
    private BigDecimal baseFare;
    private BigDecimal pricePerKm;
    private String defaultCurrency;
    private boolean driverValidationRequired;
}
