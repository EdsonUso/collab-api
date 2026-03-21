package com.edsonuso.collabapi.content.repository;

import com.edsonuso.collabapi.content.entity.UserContentPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserContentPreferenceRepository extends JpaRepository<UserContentPreferences, Long> {
    void deleteAllByUser_PublicId(String userId);
}
