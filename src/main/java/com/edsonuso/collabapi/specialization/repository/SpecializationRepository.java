package com.edsonuso.collabapi.specialization.repository;

import com.edsonuso.collabapi.specialization.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Short> {
    Optional<Specialization> findBySlug(String slug);

    List<Specialization> findBySlugInAndActiveTrue(Set<String> slugs);

    List<Specialization> findByActiveTrueOrderByDisplayOrderAsc();

    long countByIdIn(List<Short> ids);

}
