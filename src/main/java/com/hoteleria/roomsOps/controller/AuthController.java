package com.hoteleria.roomsOps.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoteleria.roomsOps.config.JwtUtil;
import com.hoteleria.roomsOps.dto.UserDto;
import com.hoteleria.roomsOps.model.User;
import com.hoteleria.roomsOps.service.UserService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwt;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje","Email y password son requeridos"));
        }

        User user = service.findUserEmail(email);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje","Credenciales inválidas"));
        }

        String token = jwt.generadorToken(user.getEmail());

        UserDto responseUser = UserDto.fromEntity(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("user", responseUser);

        return ResponseEntity.ok(resp);
    }
    
}
