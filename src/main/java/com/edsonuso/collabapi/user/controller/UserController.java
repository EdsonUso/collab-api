package com.edsonuso.collabapi.user.controller;

import com.edsonuso.collabapi.common.command.ApiResponse;
import com.edsonuso.collabapi.config.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.edsonuso.collabapi.user.command.UserCommands;
import com.edsonuso.collabapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints de usuários")
public class UserController {

    private final UserService userService;

    @GetMapping("/check-username/{username}")
    @Operation(summary = "Verificar disponibilidade de username")
    public ResponseEntity<ApiResponse<UserCommands.UsernameAvailabilityResponse>> checkUsername(
            @PathVariable String username,
            @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = false) AuthenticatedUser user
    ) {
        String publicId = user != null ? user.publicId() : null;
        return ResponseEntity.ok(ApiResponse.ok(userService.checkUsernameAvailability(username, publicId)));
    }
}
