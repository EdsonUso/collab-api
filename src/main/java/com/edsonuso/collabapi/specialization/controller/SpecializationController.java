package com.edsonuso.collabapi.specialization.controller;

import com.edsonuso.collabapi.common.command.ApiResponse;
import com.edsonuso.collabapi.config.security.JwtAuthenticationFilter;
import com.edsonuso.collabapi.specialization.command.SpecializationCommands;
import com.edsonuso.collabapi.specialization.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Especializações", description = "Endpoints para gestão de especializações profissionais")
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping("/auth/specializations")
    @Operation(summary = "Listar todas as especializações", description = "Retorna o catálogo completo de especializações disponíveis")
    public ResponseEntity<ApiResponse<List<SpecializationCommands.SpecializationResponse>>> listAll() {
        List<SpecializationCommands.SpecializationResponse> specs = specializationService.listAll();
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @GetMapping("/users/me/specializations")
    @Operation(summary = "Obter minhas especializações", description = "Retorna as especializações associadas ao usuário logado")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Especializações recuperadas com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> getMySpecializations(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal
            ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.getUserSpecializations(principal.publicId());
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @PutMapping("/users/me/specializations")
    @Operation(summary = "Atualizar minhas especializações", description = "Define as especializações do usuário logado (máximo 5)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Especializações atualizadas com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> updateMySpecializations(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal,
            @Valid @RequestBody SpecializationCommands.UpdateSpecializationsRequest request
            ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.updateUserSpecializations(
                principal.publicId(), request
        );
        return ResponseEntity.ok(ApiResponse.ok("Especializações atualizadas com sucesso", specs));
    }

    @GetMapping("/users/{publicId}/specializations")
    @Operation(summary = "Obter especializações de um usuário", description = "Retorna as especializações de um usuário específico pelo ID público")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Especializações recuperadas com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<SpecializationCommands.UserSpecializationResponse>>> getUserSpecializations(
            @PathVariable String publicId
    ) {
        List<SpecializationCommands.UserSpecializationResponse> specs = specializationService.getUserSpecializations(publicId);
        return ResponseEntity.ok(ApiResponse.ok(specs));
    }

    @GetMapping("/users/me/collaborators")
    @Operation(summary = "Encontrar colaboradores", description = "Retorna IDs de usuários que possuem especializações compatíveis")
    public ResponseEntity<ApiResponse<List<String>>> findCollaborators(
            @AuthenticationPrincipal JwtAuthenticationFilter.AuthenticatedUser principal
    ) {
        List<String> collaboratorIds = specializationService.findCollaborators(principal.publicId());
        return ResponseEntity.ok(ApiResponse.ok(collaboratorIds));
    }

}
