package com.edsonuso.collabapi.onboarding.controller;

import com.edsonuso.collabapi.common.command.ApiResponse;
import com.edsonuso.collabapi.config.security.JwtAuthenticationFilter.AuthenticatedUser;
import com.edsonuso.collabapi.onboarding.commands.UserOnboardingCommands;
import com.edsonuso.collabapi.onboarding.service.UserOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
@Tag(name = "Onboarding", description = "Endpoints para acompanhamento do processo de boas-vindas do usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserOnboardingController {

    private final UserOnboardingService onboardingService;

    @GetMapping("/status")
    @Operation(summary = "Consultar status do onboarding")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> getStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.getOnboardingStatus(user.publicId())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Realizar etapa de Profile do Onboarding")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> updateOnboardingProfileStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UserOnboardingCommands.UserOnboardingProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.updateProfileStep(user.publicId(), request)));
    }

    @PutMapping("/specializations")
    @Operation(summary = "Realizar etapa de Specializations do Onboarding")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> updateOnboardingSpecializationStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UserOnboardingCommands.UserOnboardingSpecializationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.updateSpecializationStep(user.publicId(), request)));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Realizar etapa de Preferences do Onboarding")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> updateOnboardingPreferencesStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UserOnboardingCommands.UserOnboardingPreferencesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.updatePreferencesStep(user.publicId(), request)));
    }

    @PutMapping("/follows")
    @Operation(summary = "Realizar etapa de Follows do Onboarding")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> updateOnboardingFollowsStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UserOnboardingCommands.UserOnboardingFollowsRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.updateFollowsStep(user.publicId(), request)));
    }

    @PostMapping("/skip")
    @Operation(summary = "Pular etapa opcional do Onboarding (PREFERENCES ou FOLLOWS)")
    public ResponseEntity<ApiResponse<UserOnboardingCommands.UserOnboardingResponse>> skipCurrentStep(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(ApiResponse.ok(onboardingService.skipCurrentStep(user.publicId())));
    }

}
