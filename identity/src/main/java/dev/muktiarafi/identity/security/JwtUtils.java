package dev.muktiarafi.identity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muktiarafi.identity.model.UserPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtils {
    private final String accessKey;
    private final String refreshKey;
    private final String kid;
    private final int expInSeconds;
    private final ObjectMapper MAPPER = new ObjectMapper();

    public JwtUtils(
            @Value("${jwt.accessKey}") String accessKey,
            @Value("${jwt.kid}") String kid,
            @Value("${jwt.refreshKey}") String refreshKey,
            @Value("${jwt.exp:7200}") int expInSeconds) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
        this.kid = kid;
        this.expInSeconds = expInSeconds;
    }

    public String generateAccessToken(UserPayload userPayload) {
        var key = Keys.hmacShaKeyFor(accessKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userPayload.getId().toString())
                .setIssuedAt(new Date())
                .setHeaderParam("kid", kid)
                .setIssuer("shop")
                .setExpiration(Date.from(Instant.now().plusSeconds(expInSeconds)))
                .setClaims(MAPPER.convertValue(userPayload, Map.class))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserPayload userPayload) {
        var key = Keys.hmacShaKeyFor(refreshKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userPayload.getId().toString())
                .setIssuedAt(new Date())
                .setClaims(MAPPER.convertValue(userPayload, Map.class))
                .signWith(key)
                .compact();
    }

    public Optional<Claims> validateAccessToken(String refreshToken) {
        try {
            var key = Keys.hmacShaKeyFor(accessKey.getBytes(StandardCharsets.UTF_8));
            var claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            return Optional.of(claim);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Claims> validateRefreshToken(String refreshToken) {
        try {
            var key = Keys.hmacShaKeyFor(refreshKey.getBytes(StandardCharsets.UTF_8));
            var claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            return Optional.of(claim);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public UserPayload parseClaims(Claims claims) {
        return MAPPER.convertValue(claims, UserPayload.class);
    }
}
