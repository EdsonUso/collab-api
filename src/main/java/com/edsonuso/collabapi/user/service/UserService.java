package com.edsonuso.collabapi.user.service;

import com.edsonuso.collabapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void updateUsername() {


    }

    public void updateUsername(String email) {
        String usernameLower = email.toLowerCase().split("@")[0];
        String usernameValid = Normalizer.normalize(usernameLower, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        log.debug(usernameValid);

    }


}
