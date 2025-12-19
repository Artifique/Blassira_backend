package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class NotificationSettingsDto {
    private AdminNotificationSettingsDto admin;
    private ChannelNotificationSettingsDto channels;

    @Data
    public static class AdminNotificationSettingsDto {
        private Boolean notifyNewUsers;
        private Boolean notifyNewTrips;
        private Boolean notifyIncidents;
        private Boolean notifyReports;
    }

    @Data
    public static class ChannelNotificationSettingsDto {
        private Boolean emailNotifications;
        private Boolean smsNotifications;
        private Boolean pushNotifications;
    }
}
