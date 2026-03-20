package com.edsonuso.collabapi.user.entity;

import com.edsonuso.collabapi.user.entity.Specialization;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.entity.UserSpecializationId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_specializations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSpecialization {

    @EmbeddedId
    private UserSpecializationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("specializationId")
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = false;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant assignedAt = Instant.now();

    // ── Factory method ──

    public static UserSpecialization of(User user, Specialization spec, boolean isPrimary) {
        return UserSpecialization.builder()
                .id(new UserSpecializationId(user.getId(), spec.getId()))
                .user(user)
                .specialization(spec)
                .primary(isPrimary)
                .build();
    }
}