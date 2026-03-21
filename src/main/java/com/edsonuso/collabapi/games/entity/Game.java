package com.edsonuso.collabapi.games.entity;

import com.edsonuso.collabapi.squad.entity.Squad;
import com.edsonuso.collabapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "games")
@Builder
@Getter
@Setter
public class Game {

    public enum Status {
        IN_DEVELOPMENT,
        RELEASED,
        PAUSED,
        ABANDONED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, length = 36, unique = true)
    private String publicId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @MapsId("squadId")
    @JoinColumn(name = "squad_id")
    private Squad squad;

    @MapsId("creatorId")
    @JoinColumn(name = "creator_id")
    private User creator;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('IN_DEVELOPMENT', 'RELEASED', 'PAUSED', 'ABANDONED') DEFAULT 'IN_DEVELOPMENT'")
    private Status status = Status.IN_DEVELOPMENT;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    @PrePersist
    private void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }

}
