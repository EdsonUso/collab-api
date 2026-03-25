package com.edsonuso.collabapi.specialization.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "specializations")
@Getter
@Setter
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true, length = 50)
    private String slug;
    @Column(length = 255)
    private String description;
    @Column(length = 50)
    private String icon;
    @Column(name = "display_order", nullable = false)
    private short displayOrder;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

}
