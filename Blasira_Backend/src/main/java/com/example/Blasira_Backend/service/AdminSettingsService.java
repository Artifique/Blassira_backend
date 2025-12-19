package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.settings.SettingsDto;
import com.example.Blasira_Backend.model.AppSetting;
import com.example.Blasira_Backend.repository.AppSettingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final AppSettingRepository appSettingRepository;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper for JSON conversion

    /**
     * Retrieves the current application settings. If no settings exist, returns default values.
     * @return The current application settings.
     */
    @Transactional(readOnly = true)
    public SettingsDto getSettings() {
        Optional<AppSetting> appSettingOptional = appSettingRepository.findById(1L); // Assuming a single settings entry with ID 1
        
        if (appSettingOptional.isPresent()) {
            try {
                return objectMapper.readValue(appSettingOptional.get().getSettingsJson(), SettingsDto.class);
            } catch (JsonProcessingException e) {
                // Log the error and return default settings or throw a custom exception
                e.printStackTrace(); // For debugging
                return getDefaultSettings();
            }
        }
        return getDefaultSettings();
    }

    /**
     * Updates the application settings.
     * @param request The new settings to apply.
     * @return The updated application settings.
     */
    @Transactional
    public SettingsDto updateSettings(SettingsDto request) {
        AppSetting appSetting = appSettingRepository.findById(1L) // Assuming a single settings entry with ID 1
                .orElse(new AppSetting()); // Create new if not found

        appSetting.setId(1L); // Ensure ID is 1 for the single entry

        try {
            appSetting.setSettingsJson(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // Log the error and throw a custom exception
            e.printStackTrace(); // For debugging
            throw new RuntimeException("Failed to serialize settings", e);
        }

        appSettingRepository.save(appSetting);
        return request; // Return the updated settings
    }

    private SettingsDto getDefaultSettings() {
        // Implement default settings here
        // This should match the structure of your SettingsDto
        // For brevity, filling only a few fields
        SettingsDto defaultSettings = new SettingsDto();
        defaultSettings.setGeneral(new com.example.Blasira_Backend.dto.admin.settings.GeneralSettingsDto());
        defaultSettings.getGeneral().setPlatformName("Blasira");
        defaultSettings.getGeneral().setPlatformEmail("admin@blasira.ml");
        defaultSettings.getGeneral().setPlatformPhone("+223 XX XX XX XX");
        defaultSettings.getGeneral().setSupportEmail("support@blasira.ml");
        defaultSettings.getGeneral().setTimezone("Africa/Bamako");
        defaultSettings.getGeneral().setLanguage("fr");

        defaultSettings.setTrips(new com.example.Blasira_Backend.dto.admin.settings.TripSettingsDto());
        defaultSettings.getTrips().setMaxPassengersPerTrip(4);
        defaultSettings.getTrips().setMinTripPrice(500);
        defaultSettings.getTrips().setMaxTripPrice(50000);
        defaultSettings.getTrips().setAutoApproveTrips(false);

        defaultSettings.setFeatures(new com.example.Blasira_Backend.dto.admin.settings.FeatureSettingsDto());
        defaultSettings.getFeatures().setEnableReviews(true);
        defaultSettings.getFeatures().setEnableChat(true);

        defaultSettings.setSecurity(new com.example.Blasira_Backend.dto.admin.settings.SecuritySettingsDto());
        defaultSettings.getSecurity().setRequireEmailVerification(true);
        defaultSettings.getSecurity().setRequirePhoneVerification(true);
        defaultSettings.getSecurity().setRequirePhotoVerification(true);
        defaultSettings.getSecurity().setAllowStudentOnly(false);
        defaultSettings.getSecurity().setDataRetentionDays(365);

        defaultSettings.setNotifications(new com.example.Blasira_Backend.dto.admin.settings.NotificationSettingsDto());
        defaultSettings.getNotifications().setAdmin(new com.example.Blasira_Backend.dto.admin.settings.NotificationSettingsDto.AdminNotificationSettingsDto());
        defaultSettings.getNotifications().getAdmin().setNotifyNewUsers(true);
        defaultSettings.getNotifications().getAdmin().setNotifyNewTrips(true);
        defaultSettings.getNotifications().getAdmin().setNotifyIncidents(true);
        defaultSettings.getNotifications().getAdmin().setNotifyReports(true);
        defaultSettings.getNotifications().setChannels(new com.example.Blasira_Backend.dto.admin.settings.NotificationSettingsDto.ChannelNotificationSettingsDto());
        defaultSettings.getNotifications().getChannels().setEmailNotifications(true);
        defaultSettings.getNotifications().getChannels().setSmsNotifications(false);
        defaultSettings.getNotifications().getChannels().setPushNotifications(true);


        defaultSettings.setPayments(new com.example.Blasira_Backend.dto.admin.settings.PaymentSettingsDto());
        defaultSettings.getPayments().setCommissionRate(10);
        defaultSettings.getPayments().setCurrency("FCFA");

        defaultSettings.setSystem(new com.example.Blasira_Backend.dto.admin.settings.SystemSettingsDto());
        defaultSettings.getSystem().setMaintenanceMode(false);
        defaultSettings.getSystem().setBackupFrequency("daily");

        return defaultSettings;
    }
}
