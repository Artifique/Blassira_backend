package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.Review;
import com.example.Blasira_Backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBooking(Booking booking);

    List<Review> findByRecipient(UserProfile userProfile);
}
