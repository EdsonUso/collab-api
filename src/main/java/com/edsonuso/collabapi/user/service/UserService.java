package com.edsonuso.collabapi.user.service;

import com.edsonuso.collabapi.common.exception.BusinessException;
import com.edsonuso.collabapi.user.command.UserCommands;
import com.edsonuso.collabapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UsernameGeneratorService usernameGeneratorService;

    @Transactional(readOnly = true)
    public UserCommands.UsernameAvailabilityResponse checkUsernameAvailability(String username, String authenticatedPublicId) {
        String regex = "^[a-z0-9]([a-z0-9]{0,28}[a-z0-9])?$";
        if (username == null || !username.matches(regex) || username.length() < 2 || username.length() > 30) {
            throw new BusinessException("Formato de username inválido", HttpStatus.BAD_REQUEST);
        }

        if (usernameGeneratorService.isReservedName(username)) {
            return new UserCommands.UsernameAvailabilityResponse(false);
        }

        if (authenticatedPublicId != null) {
            boolean takenByOther = userRepository.findByUsername(username)
                    .map(u -> !u.getPublicId().equals(authenticatedPublicId))
                    .orElse(false);
            return new UserCommands.UsernameAvailabilityResponse(!takenByOther);
        }

        boolean exists = userRepository.existsByUsername(username);
        return new UserCommands.UsernameAvailabilityResponse(!exists);
    }
}
