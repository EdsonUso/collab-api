package com.edsonuso.collabapi.auth.controller;

import com.edsonuso.collabapi.auth.AuthDtos;
import com.edsonuso.collabapi.auth.service.AuthService;
import com.edsonuso.collabapi.common.command.ApiResponse;
import com.edsonuso.collabapi.config.security.JwtAuthenticationFilter.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para gestão de identidade e acesso")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário com email e senha")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> register(
            @Valid @RequestBody AuthDtos.RegisterRequest request
            ){
        AuthDtos.AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cadastro realizado com sucesso", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Realiza login com email e senha retornando tokens de acesso")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> login(
            @Valid @RequestBody AuthDtos.LoginRequest request
            ) {
        AuthDtos.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Utiliza um refresh token válido para obter um novo access token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> refresh(
            @Valid @RequestBody AuthDtos.RefreshTokenRequest request
    ) {
        AuthDtos.AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Revoga os tokens de acesso do usuário")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        authService.logout(principal.publicId());
        return ResponseEntity.ok(ApiResponse.ok("Logout realizado com sucesso", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado", description = "Retorna informações básicas do perfil do usuário autenticado")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dados recuperados com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<AuthenticatedUser>> me(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok(principal));
    }
}
