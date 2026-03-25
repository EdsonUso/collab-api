package com.edsonuso.collabapi.user.service;

import com.edsonuso.collabapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsernameGeneratorService {

    private final UserRepository userRepository;
    private final Set<String> reservedNames = Set.of("admin", "api", "settings", "login", "register", "collab");

    public String generate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot e null or blank");
        }

        String lowerUsername = email.toLowerCase().split("@")[0];
        String validUsername = Normalizer.normalize(lowerUsername, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (validUsername.isBlank()) {
            validUsername = "user";
        }

        log.debug("Base username generated: {}", validUsername);
        String candidate = validUsername;

        int suffix = 1;

        while (reservedNames.contains(candidate) || userRepository.existsByUsername(candidate)) {
            candidate = validUsername + suffix;
            suffix++;
        }
        return candidate;
    }

    public boolean isReservedName(String candidate) {
        return reservedNames.contains(candidate);
    }
}
