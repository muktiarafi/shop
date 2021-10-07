package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.exception.ResourceConflictException;
import dev.muktiarafi.identity.exception.ResourceNotFoundException;
import dev.muktiarafi.identity.exception.UnprocessableEntityException;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterDto registerDto) {
        if (registerDto.getEmail() == null && registerDto.getPhoneNumber() == null) {
            throw new UnprocessableEntityException("Email or Phone number is required");
        }
        User actualUser = null;
        try {
            var hash = passwordEncoder.encode(registerDto.getPassword());
            registerDto.setPassword(hash);
            var currentUser = userMapper.registerDtoToUser(registerDto);
            actualUser = userRepository.save(currentUser);
        } catch (DataIntegrityViolationException ex) {
            handleDataIntegrityException(ex);
        }

        return actualUser;
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void handleDataIntegrityException(DataIntegrityViolationException ex) {
        String constraintName = null;
        if ((ex.getCause() != null) && (ex.getCause() instanceof ConstraintViolationException)) {
            constraintName = ((ConstraintViolationException) ex.getCause()).getConstraintName();

            switch (constraintName) {
                case "users_email_key":
                    throw new ResourceConflictException("Email already used");
                case "users_phone_number_key":
                    throw new ResourceConflictException("Phone number already used");
            }
        }

        throw ex;
    }
}
