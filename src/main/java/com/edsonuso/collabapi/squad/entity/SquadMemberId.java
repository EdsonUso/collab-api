package com.edsonuso.collabapi.squad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SquadMemberId {

    @Column(name = "squad_id", nullable = false)
    private Long squadId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}
