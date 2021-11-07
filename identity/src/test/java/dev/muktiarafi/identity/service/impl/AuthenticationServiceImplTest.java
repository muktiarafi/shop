package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.LoginDto;
import dev.muktiarafi.identity.dto.RefreshTokenDto;
import dev.muktiarafi.identity.entity.Role;
import dev.muktiarafi.identity.entity.Token;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.model.RoleEnum;
import dev.muktiarafi.identity.repository.RoleRepository;
import dev.muktiarafi.identity.repository.TokenRepository;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.security.JwtUtils;
import dev.muktiarafi.identity.service.AuthenticationService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class AuthenticationServiceImplTest {

    @MockBean
    RoleRepository roleRepository;

    @Mock
    AuthenticationService authenticationService;

    @Autowired
    ApplicationContext context;

    @Mock
    UserRepository userRepository;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    PasswordEncoder passwordEncoder;

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void setup() {
        authenticationService = new AuthenticationServiceImpl(userRepository, userMapper, tokenRepository, passwordEncoder, jwtUtils);
        var mockRoleRepository = context.getBean(RoleRepository.class);
        ReflectionTestUtils.setField(userMapper, "roleRepository", mockRoleRepository);
    }

    @Test
    @DisplayName("Should authenticate")
    void shouldAuthenticate() {
        var loginDto = LoginDto.builder()
                .email("bambank@mail.com")
                .phoneNumber("+6123124124")
                .password("werwegwerwer")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(loginDto.getEmail())
                .password(loginDto.getPassword())
                .roles(List.of(role))
                .build();
        var accessToken = "access token";
        var refreshToken = "refreshToken";

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateAccessToken(any())).thenReturn(accessToken);
        when(jwtUtils.generateRefreshToken(any())).thenReturn(refreshToken);
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(tokenRepository.save(any())).thenReturn(new Token());

        var tokenDto = authenticationService.authenticate(loginDto);

        assertThat(tokenDto.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(tokenDto.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("Should authenticate using email")
    void shouldAuthenticateUsingEmail() {
        var loginDto = LoginDto.builder()
                .phoneNumber("+6123124124")
                .password("werwegwerwer")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(loginDto.getEmail())
                .password(loginDto.getPassword())
                .roles(List.of(role))
                .build();
        var accessToken = "access token";
        var refreshToken = "refreshToken";

        when(userRepository.findByPhoneNumber(loginDto.getPhoneNumber())).thenReturn(Optional.of(user));
        when(jwtUtils.generateAccessToken(any())).thenReturn(accessToken);
        when(jwtUtils.generateRefreshToken(any())).thenReturn(refreshToken);
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(tokenRepository.save(any())).thenReturn(new Token());

        var tokenDto = authenticationService.authenticate(loginDto);

        assertThat(tokenDto.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(tokenDto.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("Should authenticate using phone number")
    void shouldAuthenticateUsingPhoneNumber() {
        var loginDto = LoginDto.builder()
                .email("bambank@mail.com")
                .password("werwegwerwer")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(loginDto.getEmail())
                .password(loginDto.getPassword())
                .roles(List.of(role))
                .build();
        var accessToken = "access token";
        var refreshToken = "refreshToken";

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateAccessToken(any())).thenReturn(accessToken);
        when(jwtUtils.generateRefreshToken(any())).thenReturn(refreshToken);
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(tokenRepository.save(any())).thenReturn(new Token());

        var tokenDto = authenticationService.authenticate(loginDto);

        assertThat(tokenDto.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(tokenDto.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("Should throw bad credential when email and password is null")
    void shouldThrowBadCredential() {
        assertThatThrownBy(() -> authenticationService.authenticate(new LoginDto()))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should throw bad credential if password not mathcees")
    void shouldThrowBadCredentialIfPasswordNotMatches() {
        var loginDto = LoginDto.builder()
                .email("bambank@mail.com")
                .password("12345678")
                .build();
        var user = User.builder()
                .password("87654321")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> authenticationService.authenticate(loginDto))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should refresh access token")
    void shouldRefreshAccessToken() {
        var accessToken = "access token";
        var refreshToken = "refresh token";
        var token = new Token(refreshToken);

        when(tokenRepository.findById(refreshToken)).thenReturn(Optional.of(token));
        when(jwtUtils.validateRefreshToken(token.getRefreshToken())).thenReturn(Optional.of(new DefaultClaims()));
        when(jwtUtils.generateAccessToken(any())).thenReturn(accessToken);

        var refreshTokenDto = new RefreshTokenDto(refreshToken);
        var tokenDto = authenticationService.refreshAccessToken(refreshTokenDto);

        assertThat(tokenDto.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenDto.getRefreshToken()).isEqualTo(refreshToken);
    }
}