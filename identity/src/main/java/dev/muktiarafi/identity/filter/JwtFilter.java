package dev.muktiarafi.identity.filter;

import dev.muktiarafi.identity.security.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = authentication(request);
        if (authentication.isEmpty()) {
            resolver.resolveException(
                    request,
                    response,
                    null,
                    new BadCredentialsException("Bad Credential"));
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication.get());
        filterChain.doFilter(request, response);
    }

    private Optional<Authentication> authentication(HttpServletRequest request) {
        var accessToken = parseJws(request);
        if (accessToken.isEmpty()) {
            return Optional.empty();
        }
        var claim = jwtUtils.validateAccessToken(accessToken.get());
        if (claim.isEmpty()) {
            return Optional.empty();
        }

        var userPayload = jwtUtils.parseClaims(claim.get());
        var authorities = userPayload.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        var principal = new User(userPayload.getUsername(), "", authorities);
        var authentication = new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
        authentication.setDetails(principal);

        return Optional.of(authentication);
    }

    private Optional<String> parseJws(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }

        return Optional.empty();
    }
}
