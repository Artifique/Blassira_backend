package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
