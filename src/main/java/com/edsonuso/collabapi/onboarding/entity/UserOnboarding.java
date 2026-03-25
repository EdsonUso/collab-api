package com.edsonuso.collabapi.onboarding.entity;

import com.edsonuso.collabapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_onboarding")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserOnboarding {
    public enum OnboardingStep {
        PROFILE,
        SPECIALIZATIONS,
        PREFERENCES,
        FOLLOWS,
        COMPLETED
    }

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false, columnDefinition = "ENUM('PROFILE','SPECIALIZATIONS','PREFERENCES','FOLLOWS','COMPLETED') DEFAULT 'PROFILE'")
    private OnboardingStep currentStep = OnboardingStep.PROFILE;

    @Column(name = "profile_completed_at")
    private Instant profileCompletedAt;

    @Column(name = "specializations_completed_at")
    private Instant specializationsCompletedAt;

    @Builder.Default
    @Column(name = "preferences_skipped", nullable = false)
    private Boolean preferencesSkipped = false;

    @Column(name = "preferences_completed_at")
    private Instant preferencesCompletedAt;

    @Builder.Default
    @Column(name = "follows_skipped", nullable = false)
    private Boolean followsSkipped = false;

    @Column(name = "follows_completed_at")
    private Instant followsCompletedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();


}
