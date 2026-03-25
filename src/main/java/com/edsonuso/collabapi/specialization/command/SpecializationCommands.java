package com.edsonuso.collabapi.specialization.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public final class SpecializationCommands {

    private SpecializationCommands() {}

    @Schema(description = "Dados para atualizar especializações do usuário")
    public record UpdateSpecializationsRequest(
            @Schema(description = "Conjunto de slugs das especializações selecionadas")
            @NotEmpty(message = "Selecione ao menos uma especialização")
            @Size(max = 5, message = "Máximo de 5 especializações")
            Set<String> slugs,

            @Schema(description = "Slug da especialização principal", example = "backend-developer")
            String primarySlug       // Slug da especialização principal (opcional)
    ) {}

    /**
     * Especialização disponível no catálogo (listagem).
     */
    @Schema(description = "Detalhes de uma especialização disponível")
    public record SpecializationResponse(
            @Schema(description = "ID interno")
            Short id,
            @Schema(description = "Nome da especialização", example = "Backend Developer")
            String name,
            @Schema(description = "Slug para identificação", example = "backend-developer")
            String slug,
            @Schema(description = "Descrição da especialização")
            String description,
            @Schema(description = "Ícone representativo")
            String icon
    ) {}

    /**
     * Especialização do usuário (com flag de primária).
     */
    @Schema(description = "Especialização associada ao usuário")
    public record UserSpecializationResponse(
            @Schema(description = "Nome da especialização")
            String name,
            @Schema(description = "Slug da especialização")
            String slug,
            @Schema(description = "Ícone representativo")
            String icon,
            @Schema(description = "Indica se é a especialização principal do usuário")
            boolean primary
    ) {}

    /**
     * Perfil resumido com especializações (para cards, badges).
     */
    @Schema(description = "Resumo do usuário com suas especializações")
    public record UserWithSpecsResponse(
            @Schema(description = "ID público do usuário")
            String publicId,
            @Schema(description = "Nome de exibição")
            String displayName,
            @Schema(description = "URL do avatar")
            String avatarUrl,
            @Schema(description = "Lista de especializações do usuário")
            List<UserSpecializationResponse> specializations
    ) {}
}