package com.edsonuso.collabapi.auth.service;

import com.edsonuso.collabapi.auth.AuthDtos;
import com.edsonuso.collabapi.auth.entity.RefreshToken;
import com.edsonuso.collabapi.auth.repository.RefreshTokenRepository;
import com.edsonuso.collabapi.common.exception.BusinessException;
import com.edsonuso.collabapi.config.security.JwtService;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .displayName(request.displayName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);
        log.info("Novo usuario registrado: publicId={}", user.getPublicId());

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

        if (user.getPasswordHash() == null) {
            throw new BusinessException(
                    "Esta conta usa login social (%s). Use o botão de provedor.".formatted(user.getAuthProvider()),
                    HttpStatus.BAD_REQUEST
            );
        };

        log.info("realizando login do usuario {}", user.getDisplayName());

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        if (!user.isActive()) {
            throw new BusinessException("Conta desativada", HttpStatus.FORBIDDEN);
        }

        log.info("Login local: publicId={}", user.getPublicId());
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshTokenRequest request) {
        String tokenHash = jwtService.hashToken(request.refreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("Refresh token inválido", HttpStatus.UNAUTHORIZED));

        if(!storedToken.isUsable()) {

            if(storedToken.isRevoked()) {
                log.warn("Refresh token reuse detected! Revoking all tokens for userId={}",
                        storedToken.getUser().getId());
                refreshTokenRepository.revokeAllByUserId(storedToken.getUser().getId());
            }
            throw new BusinessException("Refresh token expirado ou revogado", HttpStatus.UNAUTHORIZED);
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        log.debug(" Token refresh: publicId={}", user.getPublicId());

        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException("Usuario não encontrado",
                        HttpStatus.NOT_FOUND));

        refreshTokenRepository.revokeAllByUserId(user.getId());
        log.info("Logout: todos os refresh tokens revogados para publicId={}", publicId);
    }

    private AuthDtos.AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getPublicId(),
                user.getEmail(),
                user.getRole().name(),
                user.getDisplayName()
        );

        log.debug("buildando acess token com valor de display name {} para usuario {}",
                user.getAuthProvider(),
                user);

        String rawRefreshToken = jwtService.generateRefreshToken();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenHash(jwtService.hashToken(rawRefreshToken))
                .user(user)
                .expiresAt(jwtService.getRefreshTokenExpiration())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        AuthDtos.UserSummary userSummary = new AuthDtos.UserSummary(
                user.getPublicId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole().name()
        );

        return new AuthDtos.AuthResponse(
                accessToken,
                rawRefreshToken,
                900,
                userSummary
        );
    }
}
