package com.edsonuso.collabapi.onboarding.commands;

import com.edsonuso.collabapi.follows.entity.Follow;
import com.edsonuso.collabapi.onboarding.entity.UserOnboarding;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;
import java.util.List;

public class UserOnboardingCommands {

    private UserOnboardingCommands() {}

    @Schema(description = "Resposta com o status do onboarding do usuário")
    public record UserOnboardingResponse(
            @Schema(description = "Passo atual do onboarding")
            UserOnboarding.OnboardingStep currentStep,
            @Schema(description = "Data de conclusão do perfil")
            Instant profileCompletedAt,
            @Schema(description = "Data de conclusão da especialização")
            Instant specializationCompletedAt,
            @Schema(description = "Indica se pulou as preferências")
            Boolean preferencesSkipped,
            @Schema(description = "Data de conclusão das preferências")
            Instant preferencesCompletedAt,
            @Schema(description = "Indica se pulou os follows")
            Boolean followsSkipped,
            @Schema(description = "Data de conclusão dos follows")
            Instant followsCompletedAt,
            @Schema(description = "Data de conclusão total do onboarding")
            Instant completedAt
    ) {}

    @Schema(description = "Request para atualização do onboarding em status PROFILE")
    public record UserOnboardingProfileRequest (
        @Schema(description = "username do usuario, gerado automaticamente e pode ser atualizado no onboarding")
        @NotBlank(message = "O username é obrigatório!")
        @Size(min = 2, max = 30, message = "O username deve ter entre 2 e 30 caracteres")
        @Pattern(
                regexp = "^[a-z0-9]([a-z0-9]{0,28}[a-z0-9])?$",
                message = "Username inválido. Use apenas letras minúsculas, números e sublinhados (não no inicio ou fim)"
        )
        String username,
        @Schema(description = "headline do usuario, ficará abaixo do nome")
        @Size(max = 150)
        String headline,
        @Schema(description = "biografia do usuario")
        @Size(max = 500)
        String bio,
        @Schema(description = "url do avatar do usuario")
        @Size(max = 500)
        @URL(message = "o formato da url do avatar é inválido")
        String avatarUrl

    ) {}

    @Schema(description = "Item de especialização com flag de primária")
    public record SpecializationItem(
            @NotNull(message = "O ID da especialização é obrigatório")
            Short specializationId,
            @NotNull(message = "O campo isPrimary é obrigatório")
            Boolean isPrimary
    ) {}

    @Schema(description = "Request para atualização do onboarding em status SPECIALIZATIONS")
    public record UserOnboardingSpecializationRequest(
            @NotNull(message = "A lista de especializações é obrigatória")
            @Valid
            List<SpecializationItem> specializations
    ) {}

    @Schema(description = "Request para atualização do onboarding em status PREFERENCES")
    public record UserOnboardingPreferencesRequest(
            @NotNull(message = "A lista de categorias é obrigatória")
            List<Long> categoryIds
    ) {}

    @Schema(description = "Item de follow")
    public record FollowItem(
            @NotNull(message = "O tipo de entidade é obrigatório")
            Follow.FollowableType followableType,
            @NotNull(message = "O ID da entidade é obrigatório")
            Long followableId
    ) {}

    @Schema(description = "Request para atualização do onboarding em status FOLLOWS")
    public record UserOnboardingFollowsRequest(
            @NotNull(message = "A lista de follows é obrigatória")
            @Valid
            List<FollowItem> follows
    ) {}

    @Schema(description = "Resposta de verificação de disponibilidade de username")
    public record UsernameAvailabilityResponse(
            @Schema(description = "Indica se o username está disponível")
            boolean available
    ) {}
}
