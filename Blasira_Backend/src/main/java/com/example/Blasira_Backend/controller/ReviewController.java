package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.review.CreateReviewRequest;
import com.example.Blasira_Backend.dto.review.ReviewDto;
import com.example.Blasira_Backend.model.Review;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/trips/{tripId}/reviews")
    public ResponseEntity<ReviewDto> createDriverReview(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserAccount currentUser) {

        Review review = reviewService.createDriverReview(tripId, currentUser.getId(), request);
        return new ResponseEntity<>(toDto(review), HttpStatus.CREATED);
    }

    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .authorId(review.getAuthor().getId())
                .authorFirstName(review.getAuthor().getFirstName())
                .recipientId(review.getRecipient().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
