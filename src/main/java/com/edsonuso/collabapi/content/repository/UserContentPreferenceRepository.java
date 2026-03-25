package com.edsonuso.collabapi.content.repository;

import com.edsonuso.collabapi.content.entity.UserContentPreferences;
import com.edsonuso.collabapi.content.entity.UserContentPreferencesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserContentPreferenceRepository extends JpaRepository<UserContentPreferences, UserContentPreferencesId> {
    @Modifying
    @Query("DELETE FROM UserContentPreferences p WHERE p.id.userId = :userId")
    void deleteAllByUserId(Long userId);
}
