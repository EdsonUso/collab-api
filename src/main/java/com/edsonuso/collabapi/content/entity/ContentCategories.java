package com.edsonuso.collabapi.content.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "content_categories")
public class ContentCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "slug", length = 50, nullable = false)
    private String slug;

    @Column(name = "icon_slug", length = 50)
    private String slug_icon;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private int display_order = 0;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}
