package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.Role;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.exception.ResourceConflictException;
import dev.muktiarafi.identity.exception.UnprocessableEntityException;
import dev.muktiarafi.identity.mapper.UserMapper;
import dev.muktiarafi.identity.model.RoleEnum;
import dev.muktiarafi.identity.repository.RoleRepository;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class UserServiceImplTest {

    @MockBean
    RoleRepository roleRepository;

    @Autowired
    ApplicationContext context;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);
        var mockRoleRepository = context.getBean(RoleRepository.class);
        ReflectionTestUtils.setField(userMapper, "roleRepository", mockRoleRepository);
    }

    @Test
    @DisplayName("Should register new user")
    void shouldRegister() {
        var registerDto = RegisterDto.builder()
                .email("bambank@mail.com")
                .name("bambank")
                .phoneNumber("+628177623405")
                .password("12345678")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .roles(List.of(role))
                .build();

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var actualUser = userService.register(registerDto);

        assertThat(actualUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(actualUser.getName()).isEqualTo(user.getName());
        assertThat(actualUser.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(actualUser.getRoles()).isNotEmpty();
        assertThat(actualUser.getRoles().get(0).getName()).isEqualTo(RoleEnum.ROLE_USER);
    }

    @Test
    @DisplayName("Should register new user using email")
    void shouldRegisterUsingEmail() {
        var registerDto = RegisterDto.builder()
                .email("bambank@mail.com")
                .name("bambank")
                .password("12345678")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .roles(List.of(role))
                .build();

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var actualUser = userService.register(registerDto);

        assertThat(actualUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(actualUser.getName()).isEqualTo(user.getName());
        assertThat(actualUser.getRoles()).isNotEmpty();
        assertThat(actualUser.getRoles().get(0).getName()).isEqualTo(RoleEnum.ROLE_USER);
    }

    @Test
    @DisplayName("Should register new user using phone number")
    void shouldRegisterUsingPhoneNumber() {
        var registerDto = RegisterDto.builder()
                .phoneNumber("+628177623405")
                .name("bambank")
                .password("12345678")
                .build();
        var role = new Role(null, RoleEnum.ROLE_USER);
        var user = User.builder()
                .email(registerDto.getEmail())
                .name(registerDto.getName())
                .roles(List.of(role))
                .build();

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        var actualUser = userService.register(registerDto);

        assertThat(actualUser.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(actualUser.getName()).isEqualTo(user.getName());
        assertThat(actualUser.getRoles()).isNotEmpty();
        assertThat(actualUser.getRoles().get(0).getName()).isEqualTo(RoleEnum.ROLE_USER);
    }

    @Test
    @DisplayName("Should throw error if phone number or email is empty")
    void shouldThrowErrorIfPhoneNumberOrEmailEmpty() {
        var registerDto = RegisterDto.builder()
                .name("bambank")
                .password("12345678")
                .build();

        assertThatThrownBy(() -> userService.register(registerDto))
                .isInstanceOf(UnprocessableEntityException.class);
    }

    @Test
    @DisplayName("Should throw resource conflict exception when constraint name is email")
    void shouldThrowResourceConflictUsingEmailConstraint() {
        var registerDto = RegisterDto.builder()
                .email("bambank@mail.com")
                .build();

        var constraintName = "users_email_key";
        var constraintViolation = new ConstraintViolationException("", null, constraintName);
        var dataIntegrityViolation = new DataIntegrityViolationException("", constraintViolation);

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(new Role()));
        when(userRepository.save(Mockito.any())).thenThrow(dataIntegrityViolation);

        assertThatThrownBy(() -> userService.register(registerDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    @DisplayName("Should throw resource conflict exception when constraint name is phone Number")
    void shouldThrowResourceConflictUsingPhoneNumberConstraint() {
        var registerDto = RegisterDto.builder()
                .phoneNumber("+6234234235135")
                .build();

        var constraintName = "users_phone_number_key";
        var constraintViolation = new ConstraintViolationException("", null, constraintName);
        var dataIntegrityViolation = new DataIntegrityViolationException("", constraintViolation);

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(new Role()));
        when(userRepository.save(Mockito.any())).thenThrow(dataIntegrityViolation);

        assertThatThrownBy(() -> userService.register(registerDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    @DisplayName("Should rethrow data integrity violation when no matching constraint name")
    void shouldRethrowDataIntegrityViolation() {
        var registerDto = RegisterDto.builder()
                .phoneNumber("+6234234235135")
                .build();

        var constraintName = "users_abogoboga_key";
        var constraintViolation = new ConstraintViolationException("", null, constraintName);
        var dataIntegrityViolation = new DataIntegrityViolationException("", constraintViolation);

        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(new Role()));
        when(userRepository.save(Mockito.any())).thenThrow(dataIntegrityViolation);

        assertThatThrownBy(() -> userService.register(registerDto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should find user using uuid")
    void shouldFindUser() {
        var user = User.builder()
                .name("bambank")
                .build();
        var userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var actualUser = userService.find(userId);
        assertThat(actualUser.getName()).isEqualTo(user.getName());
    }
}