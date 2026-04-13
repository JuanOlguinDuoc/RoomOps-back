package com.hoteleria.roomsOps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hoteleria.roomsOps.dto.ApartmentDto;
import com.hoteleria.roomsOps.service.ApartmentService;

@RestController
@RequestMapping("api/v1/apartments")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @GetMapping
    public List<ApartmentDto> listApartments(){
        return apartmentService.getApartments();
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createApartment(@RequestBody ApartmentDto dto){
        Map<String,Object> resp = new HashMap<>();
        try{
            ApartmentDto created = apartmentService.createApartment(dto);
            resp.put("message", "Apartamento creado correctamente");
            resp.put("apartment", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e){
            resp.put("message", "Error al crear apartamento");
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getApartment(@PathVariable Long id){
        ApartmentDto dto = apartmentService.findById(id);
        if (dto == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message","Apartamento no encontrado"));
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateApartment(@PathVariable Long id, @RequestBody ApartmentDto dto){
        try{
            ApartmentDto updated = apartmentService.updateApartment(id, dto);
            return ResponseEntity.ok(
                Map.of("message","Apartamento actualizado","apartment", updated)
            );
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al actualizar apartamento","error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Object> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo){
        try {
            ApartmentDto updated = apartmentService.updateEstado(id, activo);
            return ResponseEntity.ok(
                Map.of("message", "Estado actualizado", "apartment", updated)
            );
        } catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Error al cambiar estado","error", e.getMessage()));
        }
    }


}
