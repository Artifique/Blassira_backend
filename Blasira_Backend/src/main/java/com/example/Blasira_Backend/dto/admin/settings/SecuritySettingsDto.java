package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class SecuritySettingsDto {
    private Boolean requireEmailVerification;
    private Boolean requirePhoneVerification;
    private Boolean requirePhotoVerification;
    private Boolean allowStudentOnly;
    private Integer dataRetentionDays;
}
