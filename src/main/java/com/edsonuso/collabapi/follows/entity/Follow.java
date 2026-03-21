package com.edsonuso.collabapi.follows.entity;

import com.edsonuso.collabapi.user.entity.User;
import com.fasterxml.jackson.databind.node.LongNode;
import jakarta.persistence.*;
import lombok.*;

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

    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private User follower;

    @Enumerated(EnumType.STRING)
    @Column(name = "followable_type", nullable = false, columnDefinition = "ENUM('USER', 'SQUAD', 'GAME')")
    private FollowableType followableType;

}
