package com.hoteleria.roomsOps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hoteleria.roomsOps.model.Apartment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {
    private Long id;
    private String nombre;
    private Integer piso;
    private Boolean activo;

    public static ApartmentDto fromEntity (Apartment apartment){
        if (apartment == null) return null;
        return ApartmentDto.builder()
        .id(apartment.getId())
        .nombre(apartment.getNombre())
        .piso(apartment.getPiso())
        .activo(apartment.getActivo())
        .build();
    }

    public static Apartment toEntity (ApartmentDto dto){
        if (dto == null) return null;
        return Apartment.builder()
            .id(dto.getId())
            .nombre(dto.getNombre())
            .piso(dto.getPiso())
            .activo(dto.getActivo())
            .build();
    }
}
