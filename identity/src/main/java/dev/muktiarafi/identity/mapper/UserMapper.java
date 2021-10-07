package dev.muktiarafi.identity.mapper;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.entity.Role;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.exception.ResourceNotFoundException;
import dev.muktiarafi.identity.model.RoleEnum;
import dev.muktiarafi.identity.model.UserPayload;
import dev.muktiarafi.identity.repository.RoleRepository;
import dev.muktiarafi.identity.security.GoogleOAuth2UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    private RoleRepository roleRepository;

    @Mapping(target = "roles", expression = "java(userRole())")
    public abstract User registerDtoToUser(RegisterDto registerDto);

    @Mapping(target = "roles", expression = "java(userRole())")
    public abstract User googleOauth2UserInfoToUser(GoogleOAuth2UserInfo googleOAuth2UserInfo);

    @Mapping(target = "roles", expression = "java(rolesName(user))")
    public abstract UserPayload userToUserPayload(User user);

    protected List<Role> userRole() {
        var role = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return List.of(role);
    }

    protected List<String> rolesName(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }
}
