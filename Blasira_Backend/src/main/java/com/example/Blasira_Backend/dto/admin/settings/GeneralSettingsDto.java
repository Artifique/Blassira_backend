package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class GeneralSettingsDto {
    private String platformName;
    private String platformEmail;
    private String platformPhone;
    private String supportEmail;
    private String timezone;
    private String language;
}
