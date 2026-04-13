package com.hoteleria.roomsOps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.hoteleria.roomsOps.model.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StatusDto {
    private Long id;
    private String nombre;

    public static StatusDto fromEntity(Status s){
        if (s == null) return null;
        return StatusDto.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .build();
    }

    public static Status toEntity(StatusDto dto){
        if (dto == null) return null;
        return Status.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .build();
    }
}
