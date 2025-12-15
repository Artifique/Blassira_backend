package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Import List
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPhoneNumber(String phoneNumber);
    List<UserAccount> findByVerificationStatus(String status);
    List<UserAccount> findByRolesContains(Role role);
}
