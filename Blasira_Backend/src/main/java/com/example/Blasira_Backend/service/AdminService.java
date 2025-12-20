package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.*;
import com.example.Blasira_Backend.exception.UserProfileNotFoundException;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.model.enums.Role;
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.model.IncidentReport;
import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.model.enums.DiscountType;
import com.example.Blasira_Backend.repository.IncidentReportRepository;
import com.example.Blasira_Backend.repository.PromoCodeRepository;
import com.example.Blasira_Backend.model.Vehicle;
import com.example.Blasira_Backend.repository.VehicleRepository;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.dto.trip.TripDto;

import java.time.LocalDate;
import java.util.Map;

import com.example.Blasira_Backend.model.AppConfig;
import com.example.Blasira_Backend.repository.AppConfigRepository;
import com.example.Blasira_Backend.service.IncidentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Blasira_Backend.dto.document.DocumentDto; // NEW
import com.example.Blasira_Backend.model.enums.DocumentType; // NEW
import com.example.Blasira_Backend.model.enums.DocumentStatus; // NEW
import com.example.Blasira_Backend.dto.message.SendMessageRequest;
import com.example.Blasira_Backend.model.AdminNotification; // NEW
import com.example.Blasira_Backend.repository.AdminNotificationRepository; // NEW
import com.fasterxml.jackson.core.JsonProcessingException; // NEW
import com.fasterxml.jackson.databind.ObjectMapper; // NEW

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final DocumentService documentService;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final AdminNotificationRepository adminNotificationRepository; // NEW
    private final ObjectMapper objectMapper; // NEW

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userAccountRepository.count();
        long totalTrips = tripRepository.count();
        long totalBookings = bookingRepository.count();

        // Calculate Daily Activity (last 30 days)
        Map<LocalDate, Long> dailyActivity = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            long newUsers = userAccountRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            long newTrips = tripRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            // You can add more metrics here, e.g., new bookings
            dailyActivity.put(date, newUsers + newTrips);
        }

        // Calculate Monthly Trends (last 12 months)
        Map<String, Map<String, Long>> monthlyTrends = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
            LocalDateTime startOfMonth = monthStart.atStartOfDay();
            LocalDateTime endOfMonth = monthStart.withDayOfMonth(monthStart.lengthOfMonth()).atTime(23, 59, 59);

            long newUsers = userAccountRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
            long newTrips = tripRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
            long newBookings = bookingRepository.countByCreatedAtBetween(startOfMonth, endOfMonth); // Assuming Booking has createdAt

            Map<String, Long> monthData = new LinkedHashMap<>();
            monthData.put("newUsers", newUsers);
            monthData.put("newTrips", newTrips);
            monthData.put("newBookings", newBookings);

            monthlyTrends.put(monthStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")), monthData);
        }


        // Fetch Recent Activities (last 5 of each)
        List<String> recentActivities = new ArrayList<>();

        // Recent Users
        userAccountRepository.findTop5ByOrderByCreatedAtDesc().forEach(user ->
            recentActivities.add("Nouvel utilisateur: " + user.getEmail() + " (" + user.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")) + ")")
        );

        // Recent Trips
        tripRepository.findTop5ByOrderByCreatedAtDesc().forEach(trip ->
            recentActivities.add("Nouveau trajet: " + trip.getDepartureAddress() + " à " + trip.getDestinationAddress() + " (" + trip.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")) + ")")
        );

        // Recent Bookings (Assuming Booking has createdAt and relevant details)
        bookingRepository.findTop5ByOrderByCreatedAtDesc().forEach(booking ->
            recentActivities.add("Nouvelle réservation: " + booking.getBookedSeats() + " place(s) sur trajet " + booking.getTrip().getId() + " (" + booking.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")) + ")")
        );

        // Sort recent activities by timestamp (descending)
        // This requires parsing timestamps from the strings, which is complex.
        // For simplicity, we'll assume they are roughly ordered by fetch, or just list them as is.
        // If exact chronological order is needed, a dedicated ActivityLog entity would be better.
        // For now, they are added in order of User, Trip, Booking fetches.

        return AdminDashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalTrips(totalTrips)
                .totalBookings(totalBookings)
                .dailyActivity(dailyActivity)
                .monthlyTrends(monthlyTrends)
                .recentActivities(recentActivities)
                .build();
    }
    
    @Transactional
    public UserDto updateUserDetails(Long userId, UpdateUserRequest request) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));

        UserProfile userProfile = userAccount.getUserProfile();
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User profile not found for user id: " + userId);
        }

        // Update UserAccount details
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            userAccount.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            userAccount.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update UserProfile details
        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            userProfile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            userProfile.setLastName(request.getLastName());
        }
        
        userAccountRepository.save(userAccount);
        userProfileRepository.save(userProfile); // Save changes to UserProfile

        return mapToUserDto(userAccount);
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

    @Transactional(readOnly = true)
    public List<UserDocumentStatusDto> getUserDocumentStatuses() {
        return userAccountRepository.findAll().stream()
                .map(userAccount -> {
                    UserProfile userProfile = userAccount.getUserProfile();
                    // Find the most relevant document for status display (e.g., latest or most critical)
                    // For simplicity, we'll take the first document found if any.
                    // Or, we can specifically look for a DRIVERS_LICENCE if that's the primary verification.
                    List<com.example.Blasira_Backend.model.Document> documents = documentRepository.findByUser(userAccount);
                    com.example.Blasira_Backend.model.Document primaryDocument = null;
                    if (!documents.isEmpty()) {
                        // Prioritize DRIVERS_LICENCE, then ID_CARD, then any other
                        primaryDocument = documents.stream()
                                .filter(doc -> doc.getDocumentType() == DocumentType.DRIVERS_LICENCE)
                                .findFirst()
                                .orElse(documents.stream()
                                        .filter(doc -> doc.getDocumentType() == DocumentType.ID_CARD)
                                        .findFirst()
                                        .orElse(documents.get(0))); // Fallback to the first one if no specific types
                    }

                    return UserDocumentStatusDto.builder()
                            .userId(userAccount.getId())
                            .firstName(userProfile != null ? userProfile.getFirstName() : null)
                            .lastName(userProfile != null ? userProfile.getLastName() : null)
                            .email(userAccount.getEmail())
                            .roles(userAccount.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                            .documentId(primaryDocument != null ? primaryDocument.getId() : null)
                            .documentStatus(primaryDocument != null ? primaryDocument.getStatus() : null)
                            .documentUploadDate(primaryDocument != null ? primaryDocument.getCreatedAt() : null)
                            .build();
                })
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

        // If the list of recipients is empty, consider this as a diffusion à tous les utilisateurs.
        List<Long> finalRecipientIds = new ArrayList<>();
        if (recipientIds == null || recipientIds.isEmpty()) {
            finalRecipientIds = userAccountRepository.findAll().stream()
                                                .map(UserAccount::getId)
                                                .filter(id -> !id.equals(adminId)) // Exclude admin from recipients if broadcasting
                                                .collect(Collectors.toList());
        } else {
            finalRecipientIds = recipientIds.stream()
                                            .filter(id -> !id.equals(adminId)) // Exclude admin from explicit recipients
                                            .collect(Collectors.toList());
        }

        // 1. Save AdminNotification for history tracking
        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setSenderAdmin(adminUser);
        adminNotification.setTitle(request.getContent().length() > 50 ? request.getContent().substring(0, 50) + "..." : request.getContent()); // Use first part of content as title
        adminNotification.setContent(request.getContent());
        adminNotification.setType(recipientIds == null || recipientIds.isEmpty() ? "BROADCAST" : "INDIVIDUAL_BATCH");
        try {
            adminNotification.setRecipientIdsJson(objectMapper.writeValueAsString(finalRecipientIds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize recipient IDs for AdminNotification", e);
        }
        adminNotificationRepository.save(adminNotification);

        // 2. Send individual messages via MessageService (retained logic)
        for (Long recipientId : finalRecipientIds) {
            SendMessageRequest singleMessageRequest = new SendMessageRequest();
            singleMessageRequest.setRecipientId(recipientId);
            singleMessageRequest.setContent(request.getContent());
            messageService.sendMessage(adminId, singleMessageRequest);
        }
    }

    @Transactional(readOnly = true)
    public List<AdminNotificationDto> getNotificationHistory() {
        List<AdminNotification> notifications = adminNotificationRepository.findByOrderBySentAtDesc();
        return notifications.stream()
                .map(this::mapToAdminNotificationDto)
                .collect(Collectors.toList());
    }

    private AdminNotificationDto mapToAdminNotificationDto(AdminNotification notification) {
        List<Long> recipientIds = new ArrayList<>();
        if (notification.getRecipientIdsJson() != null && !notification.getRecipientIdsJson().isEmpty()) {
            try {
                recipientIds = objectMapper.readValue(notification.getRecipientIdsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            } catch (JsonProcessingException e) {
                // Log the error but continue with empty list
                System.err.println("Error deserializing recipient IDs for notification " + notification.getId() + ": " + e.getMessage());
            }
        }

        String senderAdminName = "Unknown Admin";
        if (notification.getSenderAdmin() != null && notification.getSenderAdmin().getUserProfile() != null) {
            UserProfile senderProfile = notification.getSenderAdmin().getUserProfile();
            senderAdminName = senderProfile.getFirstName() + " " + senderProfile.getLastName();
        } else if (notification.getSenderAdmin() != null) {
            senderAdminName = notification.getSenderAdmin().getEmail();
        }

        return AdminNotificationDto.builder()
                .id(notification.getId())
                .senderAdminId(notification.getSenderAdmin().getId())
                .senderAdminName(senderAdminName)
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .recipientIds(recipientIds)
                .sentAt(notification.getSentAt())
                .build();
    }
}
