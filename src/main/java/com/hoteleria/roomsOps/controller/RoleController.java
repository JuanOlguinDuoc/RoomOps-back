package com.hoteleria.roomsOps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hoteleria.roomsOps.service.RoleService;
import com.hoteleria.roomsOps.dto.RoleDto;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleDto> listRoles(){
        return roleService.getRoles();
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createRole(@RequestBody RoleDto dto){
        Map<String,Object> resp = new HashMap<>();
        try{
            RoleDto created = roleService.createRole(dto);
            resp.put("mensaje", "Rol generado correctamente");
            resp.put("role", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e){
            resp.put("mensaje", "Error al crear rol");
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRole(@PathVariable Long id){
        RoleDto dto = roleService.findById(id);
        if (dto == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje","Rol no encontrado"));
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Long id, @RequestBody RoleDto dto){
        try{
            RoleDto updated = roleService.updateRole(id, dto);
            return ResponseEntity.ok(Map.of("mensaje","Rol actualizado","role", updated));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje","Error al actualizar rol","error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRole(@PathVariable Long id){
        try{
            roleService.deleteRole(id);
            return ResponseEntity.ok(Map.of("mensaje","Rol eliminado"));
        } catch (Exception e){

            // en caso de que sea Null, le asigno un mensaje genérico para evitar NullPointerException al acceder a getMessage()
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";

            if (errorMsg.toLowerCase().contains("no encontrado")) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje","Rol no encontrado","error", errorMsg));
            }
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje","Error al eliminar rol","error", errorMsg));
        }
    }
}

