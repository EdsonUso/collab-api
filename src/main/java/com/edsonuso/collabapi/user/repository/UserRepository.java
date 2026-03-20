package com.edsonuso.collabapi.user.repository;

import com.edsonuso.collabapi.user.entity.AuthProvider;
import com.edsonuso.collabapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPublicId(String publicId);
    Optional<User> findByAuthProviderAndProviderId(AuthProvider provider, String providerId);
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
