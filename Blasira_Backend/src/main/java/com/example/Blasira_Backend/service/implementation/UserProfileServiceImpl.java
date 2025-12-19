package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.profile.ProfileDto;
import com.example.Blasira_Backend.dto.profile.UpdateUserProfileRequest;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.repository.UserProfileRepository;
import com.example.Blasira_Backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getMyProfile(UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByPhoneNumber(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
        return mapToProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileDto updateMyProfile(UpdateUserProfileRequest request, UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByPhoneNumber(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        UserProfile profile = user.getUserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToProfileDto(updatedProfile.getUserAccount());
    }

    private ProfileDto mapToProfileDto(UserAccount user) {
        UserProfile profile = user.getUserProfile();
        DriverProfile driverProfile = user.getDriverProfile();

        ProfileDto.ProfileDtoBuilder builder = ProfileDto.builder()
                .userId(user.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl());

        if (driverProfile != null) {
            builder.note(driverProfile.getAverageRating())
                   .nombreDeTrajet((long) driverProfile.getTotalTripsDriven())
                   .driverStatus(driverProfile.getStatus());
        } else {
            builder.driverStatus(DriverProfileStatus.NOT_SUBMITTED);
        }

        return builder.build();
    }
}
