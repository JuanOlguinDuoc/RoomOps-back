package com.hoteleria.roomsOps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwt) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**","/database/**", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/**")
                            .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**")
                            .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**")
                            .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**")
                            .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/roles/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/roles/**")
                            .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/roles/**")
                            .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/roles/**")
                            .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/apartments/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/apartments/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/apartments/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/apartments/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/tasks/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tasks/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tasks/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tasks/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/status/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "TRABAJADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/status/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/status/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/status/**")
                            .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                        .anyRequest().authenticated()
                       )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
