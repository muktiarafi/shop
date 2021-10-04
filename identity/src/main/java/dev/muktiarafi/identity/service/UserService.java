package dev.muktiarafi.identity.service;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.User;

public interface UserService {
    User register(RegisterDto registerDto);
    User find(String username);
}
