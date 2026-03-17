package com.edsonuso.collabapi.config.oauth2;

import com.edsonuso.collabapi.user.entity.AuthProvider;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, oidcUser.getAttributes());

        User user = processOAuth2User(userInfo);

        Map<String, Object> enrichedAttributes = new HashMap<>(oidcUser.getAttributes());
        enrichedAttributes.put("collab_user_id", user.getId());
        enrichedAttributes.put("collab_public_id", user.getPublicId());
        enrichedAttributes.put("collab_email", user.getEmail());
        enrichedAttributes.put("collab_role", user.getRole().name());
        enrichedAttributes.put("collab_display_name", user.getDisplayName());

        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
        ) {
            @Override
            public Map<String, Object> getAttributes() {
                return enrichedAttributes;
            }
        };
    }

    private User processOAuth2User(OAuth2UserInfo userInfo) {
        AuthProvider provider = userInfo.provider();
        String providerId = userInfo.id();
        String email = userInfo.email();

        Optional<User> existingByProvider = userRepository
                .findByAuthProviderAndProviderId(provider, providerId);

        if (existingByProvider.isPresent()) {
            return updateExistingUser(existingByProvider.get(), userInfo);
        }

        if (email != null) {
            Optional<User> existingByEmail = userRepository.findByEmail(email);

            if (existingByEmail.isPresent()) {
                User user = existingByEmail.get();

                if (user.getAuthProvider() == AuthProvider.LOCAL) {
                    log.info("Vinculando OAuth {} ao usuário local: {}", provider, user.getEmail());
                    user.setAuthProvider(provider);
                    user.setProviderId(providerId);
                    user.setEmailVerified(true);
                    if (user.getAvatarUrl() == null) {
                        user.setAvatarUrl(userInfo.avatarUrl());
                    }
                    return userRepository.saveAndFlush(user);
                }

                throw new OAuth2AuthenticationException(
                        "Este email já está vinculado a outro método de login: " + user.getAuthProvider()
                );
            }
        }

        if (email == null) {
            throw new OAuth2AuthenticationException(
                    "O provedor " + provider + " não retornou um email. "
                    + "Verifique se o email é público nas configurações da sua conta."
            );
        }

        String displayName = userInfo.name() != null ? userInfo.name() : email;

        log.info("Criando novo usuário via OAuth {}: {}", provider, email);
        User newUser = User.builder()
                .email(email)
                .displayName(displayName)
                .avatarUrl(userInfo.avatarUrl())
                .authProvider(provider)
                .providerId(providerId)
                .emailVerified(true)
                .build();

        return userRepository.saveAndFlush(newUser);
    }

    private User updateExistingUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.avatarUrl() != null && !userInfo.avatarUrl().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(userInfo.avatarUrl());
        }
        if (userInfo.name() != null && !userInfo.name().equals(user.getDisplayName())) {
            user.setDisplayName(userInfo.name());
        }
        return userRepository.saveAndFlush(user);
    }
}
