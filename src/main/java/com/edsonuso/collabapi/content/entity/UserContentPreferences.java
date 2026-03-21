package com.edsonuso.collabapi.content.entity;

import com.edsonuso.collabapi.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_content_preferences")
public class UserContentPreferences {

    @EmbeddedId
    private UserContentPreferencesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private ContentCategories contentCategories;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createAt = Instant.now();

}
