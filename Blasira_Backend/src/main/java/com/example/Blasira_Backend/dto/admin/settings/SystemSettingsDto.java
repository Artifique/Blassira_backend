package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class SystemSettingsDto {
    private Boolean maintenanceMode;
    private String backupFrequency;
}
