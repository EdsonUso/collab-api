package com.edsonuso.collabapi.specialization.repository;

import com.edsonuso.collabapi.specialization.entity.Specialization;
import com.edsonuso.collabapi.specialization.entity.UserSpecializationId;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edsonuso.collabapi.specialization.entity.UserSpecialization;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSpecializationRepository extends JpaRepository<UserSpecialization, UserSpecializationId> {

    List<UserSpecialization> findByUserId(Long userId);

    @Query("""
            SELECT u.publicId FROM User u
            JOIN u.specializations us
            WHERE us.specialization.slug = :slug
            AND u.active = true
            """)
    List<String> findUserPublicIdsBySpecializationSlug(String slug);

    @Query("""
            SELECT DISTINCT u.publicId FROM User u
            JOIN u.specializations us
            WHERE us.specialization.id IN (
                SELECT us2.specialization.id FROM UserSpecialization us2
                WHERE us2.user.id = :userId
            )
            AND u.id != :userId
            AND u.active = true
            """)
    List<String> findUsersWithSharedSpecializations(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserSpecialization us WHERE us.user.id = :userId")
    void deleteAllByUserId(Long userId);
}