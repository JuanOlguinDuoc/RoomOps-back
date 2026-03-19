package com.hoteleria.roomsOps.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hoteleria.roomsOps.dto.UserDto;
import com.hoteleria.roomsOps.service.UserService;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDto> userList() {
        return service.getUsers()
                      .stream()
                      .map(UserDto::fromEntity)
                      .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDto dto) {
        Map<String, Object> resp = new HashMap<>();
        try {
            UserDto created = service.createUser(dto);
            resp.put("message", "Usuario generado correctamente");
            resp.put("user", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            resp.put("message", "Error al crear usuario");
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/by-email")
    public ResponseEntity<Object> getUser(@RequestParam String email) {
        UserDto dto = service.findByEmail(email);

        if (dto == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message","Usuario no encontrado"));
        }

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Email y password son requeridos"));
        }

        UserDto user = service.findByEmail(email);

        if (user == null || !password.equals(user.getPassword())) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message","Credenciales inválidas"));
        }

        String token = "token-" + user.getEmail() + "-" + System.currentTimeMillis();

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("user", user);

        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDto dto){
        try{
            UserDto updated = service.updateUser(id, dto);
            return ResponseEntity.ok(Map.of("message","Usuario actualizado","user", updated));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al actualizar usuario","error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id){
        try{
            service.deleteUser(id);
            return ResponseEntity.ok(Map.of("message","Usuario eliminado"));
        } catch (Exception e){
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","Usuario no encontrado","error", e.getMessage()));
            }
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al eliminar usuario","error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        try{
            updates.remove("id");
            updates.remove("run");
            updates.remove("role");

            UserDto patched = service.patchUser(id, updates);

            return ResponseEntity.ok(Map.of("message","Usuario parchado","user", patched));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al parchado usuario","error", e.getMessage()));
        }
    }
}
