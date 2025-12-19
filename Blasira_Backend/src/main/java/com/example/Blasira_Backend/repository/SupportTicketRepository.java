package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.SupportTicket;
import com.example.Blasira_Backend.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // NEW

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);

    @Query("SELECT st FROM SupportTicket st JOIN st.user u JOIN u.userProfile up " +
           "WHERE (:status IS NULL OR st.status = :status) " +
           "AND (:search IS NULL OR LOWER(st.subject) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(up.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(up.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupportTicket> findByStatusAndSearch(@Param("status") TicketStatus status, 
                                             @Param("search") String search, 
                                             Pageable pageable);
}

