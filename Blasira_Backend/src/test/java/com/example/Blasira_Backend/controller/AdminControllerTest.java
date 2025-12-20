package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.config.AppConfig;
import com.example.Blasira_Backend.config.DefaultAdminInitializer;
import com.example.Blasira_Backend.controller.BookingController;
import com.example.Blasira_Backend.controller.DriverController;
import com.example.Blasira_Backend.controller.IncidentReportController;
import com.example.Blasira_Backend.controller.MessageController;
import com.example.Blasira_Backend.controller.PaymentController;
import com.example.Blasira_Backend.controller.PublicTripController;
import com.example.Blasira_Backend.controller.ReviewController;
import com.example.Blasira_Backend.dto.admin.VerificationRequestDto; // Import for DTO
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.service.AdminService;
import com.example.Blasira_Backend.service.LocationCacheService;
import com.example.Blasira_Backend.service.MessageService;
import com.example.Blasira_Backend.service.PaymentService;
import com.example.Blasira_Backend.service.ReviewService;
import com.example.Blasira_Backend.service.SupportTicketService; // NEW
import com.example.Blasira_Backend.repository.AppSettingRepository; // NEW
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser; // Import for @WithMockUser
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections; // Import for Collections.emptyList()

import static org.mockito.Mockito.when; // Import for Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // Import for MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Import for MockMvcResultMatchers.status

@WebMvcTest(controllers = AdminController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        AppConfig.class,
        DefaultAdminInitializer.class,
        BookingController.class,
        DriverController.class,
        IncidentReportController.class,
        MessageController.class,
        com.example.Blasira_Backend.service.MessageService.class,
        PaymentController.class,
        com.example.Blasira_Backend.service.PaymentService.class,
        PublicTripController.class,
        SharedTripLinkRepository.class,
        com.example.Blasira_Backend.service.LocationCacheService.class,
        ReviewController.class,
        com.example.Blasira_Backend.service.ReviewService.class,
        com.example.Blasira_Backend.repository.ReviewRepository.class,
        SupportTicketController.class // NEW
    })
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;
    @MockBean private SupportTicketService supportTicketService; // NEW

    // Additional MockBeans for services injected in AdminController or its dependencies
    // (This part might need to be expanded based on what AdminService depends on directly
    // or what WebMvcTest tries to load and fails due to missing beans)
    @MockBean private UserAccountRepository userAccountRepository;
    @MockBean private TripRepository tripRepository;
    @MockBean private BookingRepository bookingRepository;
    @MockBean private UserProfileRepository userProfileRepository;
    @MockBean private DriverProfileRepository driverProfileRepository;
    @MockBean private VehicleRepository vehicleRepository;
    @MockBean private AppConfigRepository appConfigRepository;
    @MockBean private IncidentReportRepository incidentReportRepository;
    @MockBean private PromoCodeRepository promoCodeRepository;
    @MockBean private DocumentRepository documentRepository;
    @MockBean private MessageRepository messageRepository; // NEW
    @MockBean private ConversationRepository conversationRepository; // NEW
    @MockBean private PaymentRepository paymentRepository; // NEW
    @MockBean private PaymentService paymentService; // NEW
    @MockBean private SharedTripLinkRepository sharedTripLinkRepository;
    @MockBean private ReviewRepository reviewRepository; // NEW
    @MockBean private ReviewService reviewService; // NEW
    @MockBean private AppSettingRepository appSettingRepository; // NEW
    @MockBean private SupportTicketRepository supportTicketRepository; // NEW
    @MockBean private SupportTicketMessageRepository supportTicketMessageRepository; // NEW


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPendingVerificationRequests() throws Exception {
        // Mock the service call
        when(adminService.getPendingVerificationRequests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/verifications"))
                .andExpect(status().isOk());
    }
}
