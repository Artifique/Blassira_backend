package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.AdminDashboardStatsDto;
import com.example.Blasira_Backend.dto.admin.DriverApplicationDto;
import com.example.Blasira_Backend.dto.admin.UserDto;
import com.example.Blasira_Backend.dto.admin.VerificationRequestDto;
import com.example.Blasira_Backend.exception.UserProfileNotFoundException;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.model.enums.Role;
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.dto.admin.CreatePromoCodeDto;
import com.example.Blasira_Backend.dto.admin.IncidentStatusUpdateDto;
import com.example.Blasira_Backend.dto.admin.PromoCodeDto;
import com.example.Blasira_Backend.model.IncidentReport;
import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.model.enums.DiscountType;
import com.example.Blasira_Backend.repository.IncidentReportRepository;
import com.example.Blasira_Backend.repository.PromoCodeRepository;
import com.example.Blasira_Backend.dto.admin.UpdateVehicleStatusDto;
import com.example.Blasira_Backend.model.Vehicle;
import com.example.Blasira_Backend.repository.VehicleRepository;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.dto.admin.FinancialReportDto;
import java.time.LocalDate;
import java.util.Map;
import com.example.Blasira_Backend.dto.admin.AppConfigDto;
import com.example.Blasira_Backend.dto.admin.UpdateAppConfigRequest;
import com.example.Blasira_Backend.model.AppConfig;
import com.example.Blasira_Backend.repository.AppConfigRepository;
import com.example.Blasira_Backend.service.IncidentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Blasira_Backend.dto.document.DocumentDto; // NEW
import com.example.Blasira_Backend.model.enums.DocumentType; // NEW
import com.example.Blasira_Backend.model.enums.DocumentStatus; // NEW
import com.example.Blasira_Backend.dto.admin.AdminMessageRequest;
import com.example.Blasira_Backend.dto.message.SendMessageRequest;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final UserProfileRepository userProfileRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final VehicleService vehicleService;
    private final IncidentReportService incidentReportService;
    private final PromoCodeRepository promoCodeRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final VehicleRepository vehicleRepository;
    private final AppConfigRepository appConfigRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService; // NEW
    private final MessageService messageService;

    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userAccountRepository.count();
        long totalTrips = tripRepository.count();
        long totalBookings = bookingRepository.count();

        return AdminDashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalTrips(totalTrips)
                .totalBookings(totalBookings)
                .build();
    }

    @Transactional(readOnly = true)
    public List<VerificationRequestDto> getPendingVerificationRequests() {
        return userAccountRepository.findByVerificationStatus("PENDING").stream()
                .map(this::mapToVerificationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VerificationRequestDto getVerificationRequestDetails(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));
        return mapToVerificationRequestDto(userAccount);
    }

    @Transactional
    public void approveVerificationRequest(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));

        // Update user's overall verification status
        userAccount.setVerificationStatus("VERIFIED");
        userAccountRepository.save(userAccount);

        // Update status of all associated documents to APPROVED
        documentRepository.findByUser(userAccount).forEach(document -> {
            document.setStatus(DocumentStatus.APPROVED); // Use enum
            document.setRejectionReason(null); // Clear any previous rejection reason
            documentRepository.save(document);
        });
    }

    @Transactional
    public void rejectVerificationRequest(Long userId, String reason) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));

        // Update user's overall verification status
        userAccount.setVerificationStatus("REJECTED");
        userAccountRepository.save(userAccount);

        // Update status of all associated documents to REJECTED and set rejection reason
        documentRepository.findByUser(userAccount).forEach(document -> {
            document.setStatus(DocumentStatus.REJECTED); // Use enum
            document.setRejectionReason(reason);
            documentRepository.save(document);
        });
    }

    /**
     * Met à jour le statut d'un document spécifique par un administrateur.
     * @param documentId L'ID du document à mettre à jour.
     * @param newStatus Le nouveau statut (APPROVED, REJECTED).
     * @param rejectionReason La raison du rejet si le statut est REJECTED.
     * @return Le DTO du document mis à jour.
     */
    @Transactional
    public DocumentDto updateDocumentStatus(Long documentId, DocumentStatus newStatus, String rejectionReason) {
        return documentService.updateDocumentStatus(documentId, newStatus, rejectionReason);
    }

    private VerificationRequestDto mapToVerificationRequestDto(UserAccount userAccount) {
        // Determine userType
        String userType = "user";
        if (userAccount.getRoles().contains(com.example.Blasira_Backend.model.enums.Role.ROLE_STUDENT)) {
            userType = "student";
        } else if (userAccount.getRoles().contains(com.example.Blasira_Backend.model.enums.Role.ROLE_DRIVER)) {
            userType = "driver";
        }

        List<VerificationRequestDto.DocumentStatusDto> documentDtos = documentRepository.findByUser(userAccount).stream()
                .map(doc -> VerificationRequestDto.DocumentStatusDto.builder()
                        .documentType(doc.getDocumentType().name())
                        .status(doc.getStatus().name()) // Use .name() for enum
                        .filePath(doc.getFilePath())
                        .build())
                .collect(Collectors.toList());

        String userName = userAccount.getUserProfile() != null ?
                userAccount.getUserProfile().getFirstName() + " " + userAccount.getUserProfile().getLastName() :
                userAccount.getEmail(); // Fallback to email if profile not found

        return VerificationRequestDto.builder()
                .userId(userAccount.getId())
                .userName(userName)
                .userEmail(userAccount.getEmail())
                .userPhone(userAccount.getPhoneNumber())
                .userType(userType)
                .submissionDate(userAccount.getCreatedAt()) // TODO: Refine to actual first document submission date if needed
                .status(userAccount.getVerificationStatus())
                .documents(documentDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DriverApplicationDto> getPendingDriverApplications() {
        return driverProfileRepository.findByStatus(DriverProfileStatus.PENDING_REVIEW)
                .stream()
                .map(this::mapToDriverApplicationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDriverStatus(Long driverId, DriverProfileStatus newStatus) {
        DriverProfile driverProfile = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new UserProfileNotFoundException("Driver profile not found with id: " + driverId));
        driverProfile.setStatus(newStatus);
        driverProfileRepository.save(driverProfile);

        // If the driver profile is being verified, add ROLE_DRIVER to the user's roles
        if (newStatus == DriverProfileStatus.VERIFIED) {
            UserAccount userAccount = driverProfile.getUserAccount();
            if (userAccount != null) {
                userAccount.getRoles().add(com.example.Blasira_Backend.model.enums.Role.ROLE_DRIVER);
                userAccountRepository.save(userAccount);
            }
        }
    }

    @Transactional
    public void updateStudentVerifiedStatus(Long userId, boolean isVerified) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));

        UserProfile userProfile = userAccount.getUserProfile();
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User profile not found for user id: " + userId);
        }

        userProfile.setStudentVerified(isVerified);
        userProfileRepository.save(userProfile);

        // Logique d'attribution/retrait du rôle ROLE_STUDENT
        if (isVerified) {
            // Vérifier si un document étudiant approuvé existe
            boolean hasApprovedStudentDocument = documentRepository.findByUser(userAccount).stream()
                    .anyMatch(doc -> (doc.getDocumentType() == DocumentType.ENROLLMENT_CERTIFICATE ||
                                      doc.getDocumentType() == DocumentType.INSTITUTIONAL_EMAIL) && // ID_CARD si elle sert de preuve si nécessaire, mais moins explicite
                              doc.getStatus() == DocumentStatus.APPROVED);

            if (hasApprovedStudentDocument) {
                userAccount.getRoles().add(Role.ROLE_STUDENT);
                userAccountRepository.save(userAccount);
            } else {
                // Si pas de document approuvé, on peut choisir de ne pas attribuer le rôle
                // ou lancer une exception/journaliser un avertissement.
                // Pour l'instant, on n'attribue simplement pas le rôle ROLE_STUDENT.
            }
        } else {
            // Si le statut d'étudiant est retiré (isVerified = false), retirer aussi le rôle ROLE_STUDENT
            userAccount.getRoles().remove(Role.ROLE_STUDENT);
            userAccountRepository.save(userAccount);
        }
    }

    @Transactional
    public void updateUserRoles(Long userId, List<com.example.Blasira_Backend.model.enums.Role> newRoles) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));
        userAccount.setRoles(new java.util.HashSet<>(newRoles));
        userAccountRepository.save(userAccount);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(UserAccount userAccount) {
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .id(userAccount.getId())
                .email(userAccount.getEmail())
                .firstName(userAccount.getUserProfile() != null ? userAccount.getUserProfile().getFirstName() : null)
                .lastName(userAccount.getUserProfile() != null ? userAccount.getUserProfile().getLastName() : null)
                .roles(userAccount.getRoles());

        DriverProfile driverProfile = userAccount.getDriverProfile();
        if (driverProfile != null) {
            builder.note(driverProfile.getAverageRating())
                    .nombreDeTrajet((long) driverProfile.getTotalTripsDriven());
        }

        return builder.build();
    }

    @Transactional(readOnly = true)
    public List<com.example.Blasira_Backend.dto.vehicle.VehicleDto> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Transactional(readOnly = true)
    public List<com.example.Blasira_Backend.dto.incident.IncidentReportDto> getAllIncidentReports() {
        return incidentReportService.getAllIncidentReports();
    }

    @Transactional
    public com.example.Blasira_Backend.dto.incident.IncidentReportDto updateIncidentReportStatus(Long reportId, IncidentStatusUpdateDto updateDto) {
        // Appelle la méthode de IncidentReportService qui gère déjà la logique et le mapping
        return incidentReportService.updateIncidentReportStatus(reportId, updateDto.getStatus());
    }

    @Transactional
    public PromoCodeDto createPromoCode(CreatePromoCodeDto promoCodeDto) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(promoCodeDto.getCode());
        promoCode.setDiscountType(DiscountType.valueOf(promoCodeDto.getDiscountType()));
        promoCode.setDiscountValue(BigDecimal.valueOf(promoCodeDto.getDiscountValue()));
        promoCode.setExpiresAt(promoCodeDto.getExpirationDate().toLocalDateTime());
        promoCode.setMaxUses(promoCodeDto.getMaxUses());
        promoCode.setUseCount(0); // Nouveau code promo, utilisé 0 fois
        promoCode.setActive(promoCodeDto.isActive());
        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
        return mapToPromoCodeDto(savedPromoCode);
    }

    @Transactional(readOnly = true)
    public List<PromoCodeDto> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(this::mapToPromoCodeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateVehicleStatus(Long vehicleId, UpdateVehicleStatusDto updateDto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        vehicle.setVerificationStatus(updateDto.getStatus());
        // Vous pourriez ajouter une logique pour stocker la raison si nécessaire
        vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public List<TripDto> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToTripDto) // Supposons qu'une méthode de mapping existe ou sera créée
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripDto getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));
        return mapToTripDto(trip);
    }

    @Transactional(readOnly = true)
    public FinancialReportDto getFinancialReport() {
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenueConfirmedBookings();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Pour l'exemple, les dépenses sont une estimation simplifiée (ex: 30% du revenu)
        BigDecimal totalExpenses = totalRevenue.multiply(BigDecimal.valueOf(0.30));
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        return FinancialReportDto.builder()
                .reportDate(LocalDate.now())
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .revenueByCategory(Map.of("Trips", totalRevenue)) // Simplifié
                .expensesByCategory(Map.of("Operational", totalExpenses)) // Simplifié
                .build();
    }

    @Transactional(readOnly = true)
    public AppConfigDto getAppConfig() {
        AppConfig config = appConfigRepository.findTopByOrderByIdAsc();
        if (config == null) {
            // Si aucune configuration n'existe, retourner une configuration par défaut ou lancer une exception
            return AppConfigDto.builder()
                    .baseFare(BigDecimal.valueOf(0.0))
                    .pricePerKm(BigDecimal.valueOf(0.0))
                    .defaultCurrency("EUR")
                    .driverValidationRequired(true)
                    .build();
        }
        return mapToAppConfigDto(config);
    }

    @Transactional
    public AppConfigDto updateAppConfig(UpdateAppConfigRequest request) {
        AppConfig config = appConfigRepository.findTopByOrderByIdAsc();
        if (config == null) {
            config = new AppConfig(); // Créer une nouvelle config si elle n'existe pas
        }
        config.setBaseFare(request.getBaseFare());
        config.setPricePerKm(request.getPricePerKm());
        config.setDefaultCurrency(request.getDefaultCurrency());
        config.setDriverValidationRequired(request.isDriverValidationRequired());
        AppConfig updatedConfig = appConfigRepository.save(config);
        return mapToAppConfigDto(updatedConfig);
    }

    private AppConfigDto mapToAppConfigDto(AppConfig config) {
        return AppConfigDto.builder()
                .baseFare(config.getBaseFare())
                .pricePerKm(config.getPricePerKm())
                .defaultCurrency(config.getDefaultCurrency())
                .driverValidationRequired(config.isDriverValidationRequired())
                .build();
    }

    private PromoCodeDto mapToPromoCodeDto(PromoCode promoCode) {
        PromoCodeDto dto = new PromoCodeDto();
        dto.setId(promoCode.getId());
        dto.setCode(promoCode.getCode());
        dto.setDiscountType(promoCode.getDiscountType());
        dto.setDiscountValue(promoCode.getDiscountValue().doubleValue());
        dto.setExpiresAt(promoCode.getExpiresAt().atZone(ZoneOffset.UTC));
        dto.setMaxUses(promoCode.getMaxUses());
        dto.setUseCount(promoCode.getUseCount());
        dto.setActive(promoCode.isActive());
        return dto;
    }

    private TripDto mapToTripDto(Trip trip) {
        TripDto dto = new TripDto();
        dto.setId(trip.getId());
        dto.setDepartureAddress(trip.getDepartureAddress());
        dto.setDestinationAddress(trip.getDestinationAddress());
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setPricePerSeat(trip.getPricePerSeat());
        dto.setAvailableSeats(trip.getAvailableSeats());
        // Mappage des champs spécifiques au TripDto existant
        String driverName = null;
        if (trip.getDriver() != null && trip.getDriver().getUserAccount() != null && trip.getDriver().getUserAccount().getUserProfile() != null) {
            driverName = trip.getDriver().getUserAccount().getUserProfile().getFirstName() + " " + trip.getDriver().getUserAccount().getUserProfile().getLastName();
        }
        dto.setDriverName(driverName);
        dto.setVehicleModel(trip.getVehicle() != null ? trip.getVehicle().getModel() : null);
        return dto;
    }

    private DriverApplicationDto mapToDriverApplicationDto(DriverProfile driverProfile) {
        // Cette méthode de mapping est une simplification. Il faudrait probablement enrichir ce DTO
        // avec plus d'informations venant de UserProfile ou UserAccount si nécessaire.
        return DriverApplicationDto.builder()
                .driverProfileId(driverProfile.getId())
                .userFirstName(driverProfile.getUserAccount().getUserProfile().getFirstName())
                .userLastName(driverProfile.getUserAccount().getUserProfile().getLastName())
                .userEmail(driverProfile.getUserAccount().getEmail())
                .status(driverProfile.getStatus())
                .build();
    }

    @Transactional
    public void sendMessageToMultipleUsers(AdminMessageRequest request, UserAccount adminUser) {
        Long adminId = adminUser.getId();
        List<Long> recipientIds = request.getRecipientIds();

        // Si la liste des destinataires est vide, considérer cela comme une diffusion à tous les utilisateurs.
        if (recipientIds == null || recipientIds.isEmpty()) {
            recipientIds = userAccountRepository.findAll().stream()
                                                .map(UserAccount::getId)
                                                .collect(Collectors.toList());
        }

        for (Long recipientId : recipientIds) {
            // Ne pas s'envoyer de message à soi-même
            if (recipientId.equals(adminId)) {
                continue;
            }
            SendMessageRequest singleMessageRequest = new SendMessageRequest();
            singleMessageRequest.setRecipientId(recipientId);
            singleMessageRequest.setContent(request.getContent());
            messageService.sendMessage(adminId, singleMessageRequest);
        }
    }
}
