package com.edsonuso.collabapi.follows.repository;

import com.edsonuso.collabapi.follows.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowableTypeAndFollowableId(Long followerId, Follow.FollowableType followableType, Long followableId);
}
