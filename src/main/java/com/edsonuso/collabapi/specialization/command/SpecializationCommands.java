package com.edsonuso.collabapi.specialization.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public final class SpecializationCommands {

    private SpecializationCommands() {}

    public record UpdateSpecializationsRequest(
            @NotEmpty(message = "Selecione ao menos uma especialização")
            @Size(max = 5, message = "Máximo de 5 especializações")
            Set<String> slugs,

            String primarySlug       // Slug da especialização principal (opcional)
    ) {}

    /**
     * Especialização disponível no catálogo (listagem).
     */
    public record SpecializationResponse(
            Short id,
            String name,
            String slug,
            String description,
            String icon
    ) {}

    /**
     * Especialização do usuário (com flag de primária).
     */
    public record UserSpecializationResponse(
            String name,
            String slug,
            String icon,
            boolean primary
    ) {}

    /**
     * Perfil resumido com especializações (para cards, badges).
     */
    public record UserWithSpecsResponse(
            String publicId,
            String displayName,
            String avatarUrl,
            List<UserSpecializationResponse> specializations
    ) {}
}