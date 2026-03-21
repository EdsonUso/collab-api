package com.edsonuso.collabapi.config.oauth2;

import com.edsonuso.collabapi.user.entity.User;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final OAuth2UserProcessingService processingService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, oidcUser.getAttributes());

        User user = processingService.process(userInfo);

        Map<String, Object> enrichedAttributes = enrichAttributes(oidcUser.getAttributes(), user);

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

    private Map<String, Object> enrichAttributes(Map<String, Object> original, User user) {
        Map<String, Object> enriched = new HashMap<>(original);
        enriched.put("collab_user_id", user.getId());
        enriched.put("collab_display_name", user.getDisplayName());
        enriched.put("collab_public_id", user.getPublicId());
        enriched.put("collab_email", user.getEmail());
        enriched.put("collab_role", user.getRole().name());
        return enriched;
    }
}
