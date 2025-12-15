package com.example.Blasira_Backend.config;

import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.model.enums.Role;
import com.example.Blasira_Backend.repository.DriverProfileRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final String adminEmail = "admin@example.com";

        if (userAccountRepository.findByEmail(adminEmail).isEmpty()) {
            // Create UserAccount
            UserAccount adminUser = new UserAccount();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("admin")); // Default password
            adminUser.setPhoneNumber("+1112223333");
            adminUser.setEmailVerified(true);
            adminUser.setPhoneVerified(true);
            adminUser.setTrustCharterAcceptedAt(LocalDateTime.now());

            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_USER);
            roles.add(Role.ROLE_ADMIN);
            adminUser.setRoles(roles);

            adminUser = userAccountRepository.save(adminUser);

            // Create UserProfile
            UserProfile adminProfile = new UserProfile();
            adminProfile.setUserAccount(adminUser);
            adminProfile.setFirstName("Admin");
            adminProfile.setLastName("User");
            adminProfile.setMemberSince(LocalDateTime.now());
            adminProfile.setStudentVerified(false);
            userProfileRepository.save(adminProfile);

            // Create DriverProfile
            DriverProfile adminDriverProfile = new DriverProfile();
            adminDriverProfile.setUserAccount(adminUser);
            adminDriverProfile.setStatus(DriverProfileStatus.NOT_SUBMITTED);
            driverProfileRepository.save(adminDriverProfile);

            System.out.println("Default admin user created: " + adminEmail);
        }
    }
}
