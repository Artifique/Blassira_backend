package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AppConfigDto {
    private BigDecimal baseFare;
    private BigDecimal pricePerKm;
    private String defaultCurrency;
    private boolean driverValidationRequired;
}
