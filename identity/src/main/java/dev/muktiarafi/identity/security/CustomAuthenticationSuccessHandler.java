package dev.muktiarafi.identity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muktiarafi.identity.dto.TokenDto;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public CustomAuthenticationSuccessHandler(
            UserRepository userRepository,
            JwtUtils jwtUtils,
            UserMapper userMapper,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }
        var oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        var attributes = oidcUser.getAttributes();
        var email = (String) attributes.get("email");
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new BadCredentialsException("Bad Credential"));
            return;
        }
        var userPayload = userMapper.userToUserPayload(user.get());
        var accessToken = jwtUtils.generateAccessToken(userPayload);
        var refreshToken = jwtUtils.generateRefreshToken(userPayload);
        var tokenDto = new TokenDto(accessToken, refreshToken);

        var mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getOutputStream().println(mapper.writeValueAsString(tokenDto));
    }
}
