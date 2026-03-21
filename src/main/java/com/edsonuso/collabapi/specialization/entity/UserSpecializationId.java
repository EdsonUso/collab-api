package com.edsonuso.collabapi.specialization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserSpecializationId {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "specialization_id")
    private Short specializationId;
}
