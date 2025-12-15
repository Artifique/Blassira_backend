package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.model.SharedTripLink;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.ShareTripService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripShareController {

    private final ShareTripService shareTripService;

    @PostMapping("/{tripId}/share")
    public ResponseEntity<ShareLinkResponse> createShareLink(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserAccount currentUser) {

        SharedTripLink link = shareTripService.createShareLink(tripId, currentUser.getId());
        String shareUrl = "/public/trips/share/" + link.getToken(); // Relative URL

        return ResponseEntity.ok(ShareLinkResponse.builder().shareUrl(shareUrl).token(link.getToken()).build());
    }

    @Data
    @Builder
    private static class ShareLinkResponse {
        private String token;
        private String shareUrl;
    }
}
