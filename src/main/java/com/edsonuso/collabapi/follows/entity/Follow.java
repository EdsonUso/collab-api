package com.edsonuso.collabapi.follows.entity;

import com.edsonuso.collabapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "follows")
@Builder
@Getter
@Setter
public class Follow {
    public enum FollowableType {
        USER,
        SQUAD,
        GAME
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @Enumerated(EnumType.STRING)
    @Column(name = "followable_type", nullable = false, columnDefinition = "ENUM('USER', 'SQUAD', 'GAME')")
    private FollowableType followableType;

    @Column(name = "followable_id", nullable = false)
    private Long followableId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

}
