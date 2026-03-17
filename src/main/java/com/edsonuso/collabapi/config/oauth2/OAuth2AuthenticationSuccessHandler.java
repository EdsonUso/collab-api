package com.edsonuso.collabapi.config.oauth2;

import com.edsonuso.collabapi.auth.entity.RefreshToken;
import com.edsonuso.collabapi.auth.repository.RefreshTokenRepository;
import com.edsonuso.collabapi.config.security.JwtService;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirect-uri}")
    private String frontendRedirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("objeto de autenticação OAuth2 {}", oAuth2User);

        Long userId = oAuth2User.getAttribute("collab_user_id");
        String displayName = oAuth2User.getAttribute("collab_display_name");
        String publicId = oAuth2User.getAttribute("collab_public_id");
        String email = oAuth2User.getAttribute("collab_email");
        String role = oAuth2User.getAttribute("collab_role");

        String accessToken = jwtService.generateAccessToken(publicId, email, role, displayName);

        String rawRefreshToken = jwtService.generateRefreshToken();

        // getReferenceById cria um proxy sem precisar de query — só precisa do ID para o FK
        User userRef = userRepository.getReferenceById(userId);
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenHash(jwtService.hashToken(rawRefreshToken))
                .user(userRef)
                .expiresAt(jwtService.getRefreshTokenExpiration())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", accessToken)
                .queryParam("refresh_token", rawRefreshToken)
                .build()
                .toUriString();

        log.info("OAuth2 login success: publicId={}, redirecting to frontend", publicId);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

