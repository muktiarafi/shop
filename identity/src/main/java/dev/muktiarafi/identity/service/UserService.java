package dev.muktiarafi.identity.service;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.User;

import java.util.UUID;

public interface UserService {
    User register(RegisterDto registerDto);
    User find(UUID userId);
}
