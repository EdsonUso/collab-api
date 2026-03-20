package com.edsonuso.collabapi.config.oauth2;

import com.edsonuso.collabapi.user.entity.AuthProvider;

import java.util.Map;

public sealed interface OAuth2UserInfo {

    String id();
    String email();
    String name();
    String avatarUrl();
    AuthProvider provider();


    static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new Google(attributes);
            case "github" -> new GitHub(attributes);
            default -> throw new IllegalArgumentException("Provider não suportado: " + registrationId);
        };
    }


    record Google(Map<String, Object> attrs) implements OAuth2UserInfo {
        @Override public String id()        { return (String) attrs.get("sub"); }
        @Override public String email()     { return (String) attrs.get("email"); }
        @Override public String name()      {
            String name = (String) attrs.get("name");
            return name != null ? name : (String) attrs.get("email");
        }
        @Override public String avatarUrl() { return (String) attrs.get("picture"); }
        @Override public AuthProvider provider() { return AuthProvider.GOOGLE; }
    }

    record GitHub(Map<String, Object> attrs) implements OAuth2UserInfo {
        @Override public String id()        { return String.valueOf(attrs.get("id")); }
        @Override public String email()     { return (String) attrs.get("email"); }
        @Override public String name()      {
            String name = (String) attrs.get("name");
            return name != null ? name : (String) attrs.get("login");
        }
        @Override public String avatarUrl() { return (String) attrs.get("avatar_url"); }
        @Override public AuthProvider provider() { return AuthProvider.GITHUB; }
    }
}
