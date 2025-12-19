package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findByOrderBySentAtDesc();
}
