package dev.muktiarafi.identity.service;

import dev.muktiarafi.identity.dto.LoginDto;
import dev.muktiarafi.identity.dto.RefreshTokenDto;
import dev.muktiarafi.identity.dto.TokenDto;

public interface AuthenticationService {
    TokenDto authenticate(LoginDto loginDto);
    TokenDto refreshAccessToken(RefreshTokenDto refreshTokenDto);
    void logout(RefreshTokenDto refreshTokenDto);
}
