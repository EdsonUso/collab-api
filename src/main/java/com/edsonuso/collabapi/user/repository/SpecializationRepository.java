package com.edsonuso.collabapi.user.repository;

import com.edsonuso.collabapi.user.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Short> {
    Optional<Specialization> findBySlug(String slug);

    List<Specialization> findBySlugIn(Set<String> slugs);

    List<Specialization> findByActiveTrueOrderByDisplayOrderAsc();

}
