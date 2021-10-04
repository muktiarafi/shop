package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.LoginDto;
import dev.muktiarafi.identity.dto.RefreshTokenDto;
import dev.muktiarafi.identity.dto.TokenDto;
import dev.muktiarafi.identity.entity.Token;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.repository.TokenRepository;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.security.JwtUtils;
import dev.muktiarafi.identity.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public TokenDto authenticate(LoginDto loginDto) {
        var user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        var userPayload = userMapper.userToUserPayload(user);
        var accessToken = jwtUtils.generateAccessToken(userPayload);
        var refreshToken = jwtUtils.generateRefreshToken(userPayload);

        tokenRepository.save(new Token(refreshToken));

        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public TokenDto refreshAccessToken(RefreshTokenDto refreshTokenDto) {
        tokenRepository.findById(refreshTokenDto.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));
        var claim = jwtUtils.validateRefreshToken(refreshTokenDto.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));
        var userPayload = jwtUtils.parseClaims(claim);
        var newAccessToken = jwtUtils.generateAccessToken(userPayload);


        return new TokenDto(newAccessToken, refreshTokenDto.getRefreshToken());
    }

    @Override
    public void logout(RefreshTokenDto refreshTokenDto) {
        tokenRepository.delete(new Token(refreshTokenDto.getRefreshToken()));
    }
}
