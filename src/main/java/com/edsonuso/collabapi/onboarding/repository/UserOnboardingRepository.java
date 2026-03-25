package com.edsonuso.collabapi.onboarding.repository;

import com.edsonuso.collabapi.onboarding.entity.UserOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOnboardingRepository extends JpaRepository<UserOnboarding, Long> {

}
