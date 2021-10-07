package dev.muktiarafi.identity.security;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleOAuth2UserInfo {
    private Map<String, Object> attributes;

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getProfileImageUrl() {
        return (String) attributes.get("picture");
    }
}
