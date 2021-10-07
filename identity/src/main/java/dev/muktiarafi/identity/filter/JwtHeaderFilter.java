package dev.muktiarafi.identity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muktiarafi.identity.model.UserPayload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class JwtHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        var jwtClaimBase64 = request.getHeader("x-jwt");
        if (jwtClaimBase64 == null || jwtClaimBase64.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        var bytes = Base64.getDecoder().decode(jwtClaimBase64.getBytes(StandardCharsets.UTF_8));
        var mapper = new ObjectMapper();

        var userPayload = mapper.readValue(bytes, UserPayload.class);

        var authorities = userPayload.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        var principal = new User(userPayload.getId().toString(), "", authorities);
        var authentication = new UsernamePasswordAuthenticationToken(principal, jwtClaimBase64, authorities);
        authentication.setDetails(principal);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
