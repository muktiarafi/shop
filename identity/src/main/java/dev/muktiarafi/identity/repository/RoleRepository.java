package dev.muktiarafi.identity.repository;

import dev.muktiarafi.identity.entity.Role;
import dev.muktiarafi.identity.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleEnum name);
}
