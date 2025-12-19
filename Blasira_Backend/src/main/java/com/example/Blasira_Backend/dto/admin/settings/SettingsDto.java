package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class SettingsDto {
    private GeneralSettingsDto general;
    private TripSettingsDto trips;
    private FeatureSettingsDto features;
    private SecuritySettingsDto security;
    private NotificationSettingsDto notifications;
    private PaymentSettingsDto payments;
    private SystemSettingsDto system;
}