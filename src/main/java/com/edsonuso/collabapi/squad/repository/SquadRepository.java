package com.edsonuso.collabapi.squad.repository;

import com.edsonuso.collabapi.squad.entity.Squad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SquadRepository extends JpaRepository<Squad, Long> {
    boolean existsByIdAndActiveTrue(Long id);
}
