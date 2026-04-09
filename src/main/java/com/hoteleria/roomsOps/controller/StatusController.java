package com.hoteleria.roomsOps.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hoteleria.roomsOps.service.StatusService;
import com.hoteleria.roomsOps.dto.StatusDto;

@RestController
@RequestMapping("api/v1/status")
public class StatusController {
    
    @Autowired
    private StatusService service;

    @GetMapping
    public List<StatusDto> listStatus(){
        return service.getAllStatus();
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createStatus(@RequestBody StatusDto dto){
        Map<String,Object> resp = new HashMap<>();
        try{
            StatusDto created = service.createStatus(dto);
            resp.put("message", "Estado generado correctamente");
            resp.put("status", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e){
            resp.put("message", "Error al crear estado");
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);    
        }
    }
     
    @GetMapping("/{id}")
    public ResponseEntity<Object> getStatus(@PathVariable Long id){
        StatusDto dto = service.getStatusById(id);
        if (dto == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message","Estado no encontrado"));
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long id, @RequestBody StatusDto dto){
        try{
            StatusDto updated = service.updateStatus(id, dto);
            return ResponseEntity.ok(Map.of("message","Estado actualizado","status", updated));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al actualizar estado", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteStatus(@PathVariable Long id){
        try{
            service.deleteStatus(id);
            return ResponseEntity.ok(Map.of("message","Estado eliminado correctamente"));
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al eliminar estado", "error", e.getMessage()));
        }
    }
}