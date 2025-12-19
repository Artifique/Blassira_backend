package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.admin.DriverApplicationDto;
import com.example.Blasira_Backend.dto.document.DocumentDto; // Changed import
import com.example.Blasira_Backend.model.Document;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.repository.DocumentRepository;
import com.example.Blasira_Backend.repository.DriverProfileRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.service.DriverService;
import com.example.Blasira_Backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final UserAccountRepository userAccountRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public void applyToBecomeDriver(UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByPhoneNumber(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        DriverProfile driverProfile = user.getDriverProfile();
        if (driverProfile != null && driverProfile.getStatus() != DriverProfileStatus.NOT_SUBMITTED && driverProfile.getStatus() != DriverProfileStatus.REJECTED) {
            throw new IllegalStateException("You have already applied to become a driver. Current status: " + driverProfile.getStatus());
        }

        if (driverProfile == null) {
            driverProfile = new DriverProfile();
            driverProfile.setUserAccount(user);
            user.setDriverProfile(driverProfile);
        }

        driverProfile.setStatus(DriverProfileStatus.PENDING_REVIEW);
        driverProfileRepository.save(driverProfile);
    }

    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, DocumentType documentType, UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByPhoneNumber(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        DriverProfile driverProfile = user.getDriverProfile();
        if (driverProfile == null || driverProfile.getStatus() != DriverProfileStatus.PENDING_REVIEW) {
            throw new IllegalStateException("You must apply to be a driver and be in PENDING_REVIEW status to upload documents.");
        }

        String filename = storageService.store(file);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();

        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFilePath(fileUrl);

        Document savedDocument = documentRepository.save(document);

        return mapToDocumentDto(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverApplicationDto> getPendingApplications() {
        List<DriverProfile> pendingProfiles = driverProfileRepository.findByStatus(DriverProfileStatus.PENDING_REVIEW);
        return pendingProfiles.stream()
                .map(this::mapToDriverApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDriverApplicationStatus(Long driverProfileId, DriverProfileStatus newStatus) {
        if (newStatus == DriverProfileStatus.PENDING_REVIEW || newStatus == DriverProfileStatus.NOT_SUBMITTED) {
            throw new IllegalArgumentException("Admin can only set status to VERIFIED or REJECTED.");
        }

        DriverProfile driverProfile = driverProfileRepository.findById(driverProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Driver profile not found."));

        driverProfile.setStatus(newStatus);
        driverProfileRepository.save(driverProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverProfileStatus getDriverStatus(UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByPhoneNumber(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        DriverProfile driverProfile = user.getDriverProfile();
        if (driverProfile == null) {
            return DriverProfileStatus.NOT_SUBMITTED;
        }
        return driverProfile.getStatus();
    }

    private DriverApplicationDto mapToDriverApplicationDto(DriverProfile driverProfile) {
        List<DocumentDto> documentDtos = driverProfile.getDocuments().stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());

        return DriverApplicationDto.builder()
                .driverProfileId(driverProfile.getId())
                .userFirstName(driverProfile.getUserAccount().getUserProfile().getFirstName())
                .userLastName(driverProfile.getUserAccount().getUserProfile().getLastName())
                .userEmail(driverProfile.getUserAccount().getEmail())
                .status(driverProfile.getStatus())
                .documents(documentDtos)
                .build();
    }

    private DocumentDto mapToDocumentDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .documentType(document.getDocumentType().name()) // Convert enum to String
                .filePath(document.getFilePath())
                .status(document.getStatus().name())
                .build();
    }
}
