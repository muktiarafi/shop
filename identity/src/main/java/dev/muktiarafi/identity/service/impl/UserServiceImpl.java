package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.exception.ResourceConflictException;
import dev.muktiarafi.identity.exception.ResourceNotFoundException;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User register(RegisterDto registerDto) {
        User actualUser = null;
        try {
            var currentUser = userMapper.registerDtoToUser(registerDto);
            actualUser = userRepository.save(currentUser);
        } catch (DataIntegrityViolationException ex) {
            handleDataIntegrityException(ex);
        }

        return actualUser;
    }

    @Override
    public User find(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void handleDataIntegrityException(DataIntegrityViolationException ex) {
        String constraintName = null;
        if ((ex.getCause() != null) && (ex.getCause() instanceof ConstraintViolationException)) {
            constraintName = ((ConstraintViolationException) ex.getCause()).getConstraintName();

            switch (constraintName) {
                case "users_username_key":
                    throw new ResourceConflictException("username already taken");
                case "users_email_key":
                    throw new ResourceConflictException("Email already used");
                case "users_phone_number_key":
                    throw new ResourceConflictException("Phone number already used");
            }
        }

        throw ex;
    }
}
