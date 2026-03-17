package com.edsonuso.collabapi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aspectj.weaver.ast.Not;

public final class AuthDtos {

    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
            String displayName,

            @NotBlank(message = "Email é obrigatorio")
            @Email(message = "Email inválido")
            String email,

            @NotBlank(message = "Senha é obrigatoria")
            @Size(min = 8, max = 72, message = "Senha deve ter entre 8 a 72 caracteres")
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email é obrigatorio")
            @Email(message = "Email inválido")
            String email,

            @NotBlank(message = "Senha é obrigatoria")
            String password
    ){}


    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token é obrigatorio")
            String refreshToken
    ) {}

    public record AuthResponse (
            String acessToken,
            String refreshToken,
            long expiresIn,
            UserSummary user
    ) {}

    public record UserSummary (
            String publicId,
            String displayName,
            String email,
            String avatarUrl,
            String role
    ) {}


}
