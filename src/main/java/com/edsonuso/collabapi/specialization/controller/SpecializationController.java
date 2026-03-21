package com.edsonuso.collabapi.specialization.controller;

import com.edsonuso.collabapi.common.command.ApiResponse;
import com.edsonuso.collabapi.config.security.JwtAuthenticationFilter;
import com.edsonuso.collabapi.specialization.command.SpecializationCommands;
import com.edsonuso.collabapi.specialization.service.SpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping("/auth/specializations")
    public ResponseEntity<ApiResponse<List<SpecializationCommands.SpecializationResponse>>> listAll() {
        List<SpecializationCommands.SpecializationResponse> specs = specializationService.listAll();
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @GetMapping("/users/me/specializations")
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> getMySpecializations(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal
            ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.getUserSpecializations(principal.publicId());
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @PutMapping("/users/me/specializations")
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> updateMySpecializations(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal,
            @Valid @RequestBody SpecializationCommands.UpdateSpecializationsRequest request
            ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.updateUserSpecializations(
                principal.publicId(), request
        );
        return ResponseEntity.ok(ApiResponse.ok("Especializações atualizadas", specs));
    }

    @GetMapping("/users/{publicId}/specializations")
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> getUserSpecializations(
            @PathVariable String publicId
    ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.getUserSpecializations(publicId);
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @GetMapping("/users/me/collaborators")
    public ResponseEntity<ApiResponse<List<String>>> findCollaborators(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal
    ) {
        List<String> collaboratorIds = specializationService.findCollaborators(principal.publicId());
        return ResponseEntity.ok(ApiResponse.ok(collaboratorIds));
    }

}
