package com.hoteleria.roomsOps.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hoteleria.roomsOps.model.User;
import com.hoteleria.roomsOps.service.UserService;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwt;

    @Autowired
    private UserService service;

    @Override
    protected void doFilterInternal (
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filtro
    ) throws ServletException,IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")){
            filtro.doFilter(request, response);
        return;}
    
        String token = header.substring(7);

        if (!jwt.validacionToken(token)){
            filtro.doFilter(request, response);
            return;
        }

        String email = jwt.obtenerCorreo(token);
        User user = service.findUserEmail(email);

        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase())));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
        }
        filtro.doFilter(request, response);
    }
    
}
