package com.edsonuso.collabapi.squad.entity;


import com.edsonuso.collabapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(name = "squad_members")
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SquadMember {

    public enum Role{
        OWNER,
        ADMIN,
        MEMBER
    }

    @EmbeddedId
    private SquadMemberId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("squadId")
    @JoinColumn(name = "squad_id")
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER'")
    private Role role = Role.MEMBER;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private Instant joinedAt;



}
