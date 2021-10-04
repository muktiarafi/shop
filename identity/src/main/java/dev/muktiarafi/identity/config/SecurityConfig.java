package dev.muktiarafi.identity.config;

import dev.muktiarafi.identity.filter.JwtFilter;
import dev.muktiarafi.identity.security.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtUtils jwtUtils;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(
            JwtUtils jwtUtils,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtils = jwtUtils;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors().and().authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling();
        http.addFilterAt(new JwtFilter(jwtUtils, handlerExceptionResolver), BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/auth")
                .mvcMatchers(HttpMethod.POST, "/users")
                .mvcMatchers("/users/test")
                .mvcMatchers("/actuator/**");
    }
}
