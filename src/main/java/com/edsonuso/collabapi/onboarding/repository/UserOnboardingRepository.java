package com.edsonuso.collabapi.onboarding.repository;

import com.edsonuso.collabapi.onboarding.entity.UserOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOnboardingRepository extends JpaRepository<UserOnboarding, Long> {
    Optional<UserOnboarding> findByUser_PublicId(String publicId);
}
