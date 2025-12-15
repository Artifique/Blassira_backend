package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "driver_profiles")
public class DriverProfile {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverProfileStatus status = DriverProfileStatus.NOT_SUBMITTED;

    @Column(name = "total_trips_driven")
    private int totalTripsDriven = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "rating_sum")
    private long ratingSum = 0;

    @Column(name = "review_count")
    private int reviewCount = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UserAccount userAccount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<Document> documents = new ArrayList<>();
}
