package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    // Custom query to find the single settings entry (assuming only one is needed)
    AppSetting findTopByOrderByIdAsc();
}
