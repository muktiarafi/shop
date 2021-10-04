package dev.muktiarafi.identity.config;

import dev.muktiarafi.identity.security.CustomAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors().and().authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/users").permitAll()
                .mvcMatchers("/auth").permitAll()
                .mvcMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .exceptionHandling();
    }
}
