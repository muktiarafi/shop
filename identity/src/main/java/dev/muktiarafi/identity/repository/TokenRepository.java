package dev.muktiarafi.identity.repository;

import dev.muktiarafi.identity.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
