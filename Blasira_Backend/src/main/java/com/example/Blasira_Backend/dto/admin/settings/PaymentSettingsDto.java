package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class PaymentSettingsDto {
    private Integer commissionRate;
    private String currency;
}
