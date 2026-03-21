package com.edsonuso.collabapi.config.oauth2;

import com.edsonuso.collabapi.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserProcessingService processingService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());

        User user = processingService.process(userInfo);

        Map<String, Object> enrichedAttributes = enrichAttributes(oAuth2User.getAttributes(), user);

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                enrichedAttributes,
                nameAttributeKey
        );
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
