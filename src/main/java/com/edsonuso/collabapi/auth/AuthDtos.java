package com.edsonuso.collabapi.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    @Schema(description = "Dados para registro de novo usuário")
    public record RegisterRequest(
            @Schema(description = "Nome de exibição", example = "Edson Uso")
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
            String displayName,

            @Schema(description = "Email do usuário", example = "edson@exemplo.com")
            @NotBlank(message = "Email é obrigatorio")
            @Email(message = "Email inválido")
            String email,

            @Schema(description = "Senha do usuário", example = "senha123456")
            @NotBlank(message = "Senha é obrigatoria")
            @Size(min = 8, max = 72, message = "Senha deve ter entre 8 a 72 caracteres")
            String password
    ) {}

    @Schema(description = "Dados para login")
    public record LoginRequest(
            @Schema(description = "Email do usuário", example = "edson@exemplo.com")
            @NotBlank(message = "Email é obrigatorio")
            @Email(message = "Email inválido")
            String email,

            @Schema(description = "Senha do usuário", example = "senha123456")
            @NotBlank(message = "Senha é obrigatoria")
            String password
    ){}

    @Schema(description = "Dados para renovação de token")
    public record RefreshTokenRequest(
            @Schema(description = "Refresh token atual")
            @NotBlank(message = "Refresh token é obrigatorio")
            String refreshToken
    ) {}

    @Schema(description = "Resposta de autenticação com tokens")
    public record AuthResponse (
            @Schema(description = "Access token JWT")
            String acessToken,
            @Schema(description = "Refresh token opaco")
            String refreshToken,
            @Schema(description = "Tempo para expiração do access token em segundos")
            long expiresIn,
            @Schema(description = "Resumo dos dados do usuário")
            UserSummary user,
            @Schema(description = "Próximo passo do onboarding (null se concluído)", example = "PROFILE")
            String onboardingStep
    ) {}

    @Schema(description = "Resumo dos dados do usuário")
    public record UserSummary (
            @Schema(description = "ID público (UUID)")
            String publicId,
            @Schema(description = "Nome de exibição")
            String displayName,
            @Schema(description = "Email do usuário")
            String email,
            @Schema(description = "URL do avatar")
            String avatarUrl,
            @Schema(description = "Role do usuário", example = "USER")
            String role
    ) {}

}
