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

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDto> listUsers(){
        return service.getUsers();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDto dto) {
        Map<String, Object> resp = new HashMap<>();
        try {
            UserDto created = service.createUser(dto);
            resp.put("mensaje", "Usuario generado correctamente");
            resp.put("user", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            resp.put("mensaje", "Error al crear usuario");
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
                .body(Map.of("mensaje","Usuario no encontrado"));
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDto dto){
        try{
            UserDto updated = service.updateUser(id, dto);
            return ResponseEntity.ok(Map.of("mensaje","Usuario actualizado","user", updated));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje","Error al actualizar usuario","error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Object> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo){
        try{
            service.updateEstado(id, activo);
            return ResponseEntity.ok(Map.of("mensaje","Estado de usuario actualizado"));
        } catch (Exception e){
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje","Usuario no encontrado","error", e.getMessage()));
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("mensaje", "Error al actualizar estado de usuario");
            resp.put("error", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resp);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        try{
            updates.remove("id");
            updates.remove("run");
            updates.remove("role");

            UserDto patched = service.patchUser(id, updates);

            return ResponseEntity.ok(Map.of("mensaje","Usuario parchado","user", patched));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje","Error al parchado usuario","error", e.getMessage()));
        }
    }
}
