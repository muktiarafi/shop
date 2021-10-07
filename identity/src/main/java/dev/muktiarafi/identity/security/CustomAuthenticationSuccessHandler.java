package dev.muktiarafi.identity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muktiarafi.identity.dto.TokenDto;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        var oidcUser = (OidcUser) authentication.getPrincipal();
        var googleOAuth2UserInfo = new GoogleOAuth2UserInfo(oidcUser.getAttributes());
        var user = userRepository.findByEmail(googleOAuth2UserInfo.getEmail());

        User actualUser;
        if (user.isEmpty()) {
            actualUser = registerNewUser(googleOAuth2UserInfo);
        } else {
            actualUser = updateUser(user.get(), googleOAuth2UserInfo);
        }

        var userPayload = userMapper.userToUserPayload(actualUser);
        var accessToken = jwtUtils.generateAccessToken(userPayload);
        var refreshToken = jwtUtils.generateRefreshToken(userPayload);
        var tokenDto = new TokenDto(accessToken, refreshToken);

        var mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getOutputStream().println(mapper.writeValueAsString(tokenDto));
    }

    private User registerNewUser(GoogleOAuth2UserInfo googleOAuth2UserInfo) {
        var user = userMapper.googleOauth2UserInfoToUser(googleOAuth2UserInfo);

        return userRepository.save(user);
    }

    private User updateUser(User user, GoogleOAuth2UserInfo googleOAuth2UserInfo) {
        user.setName(googleOAuth2UserInfo.getName());
        user.setProfileImageUrl(googleOAuth2UserInfo.getProfileImageUrl());

        return user;
    }
}
