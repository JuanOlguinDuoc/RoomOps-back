package com.hoteleria.roomsOps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hoteleria.roomsOps.model.Task;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TaskDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private Long apartmentId;
    private Long assignedToId;
    private StatusDto status;

    public static TaskDto fromEntity(Task t){
        if (t == null) return null;
        return TaskDto.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .descripcion(t.getDescripcion())
                .apartmentId(t.getApartment().getId())
                .assignedToId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .status(StatusDto.fromEntity(t.getStatus()))
                .build();
    }

    public static Task toEntity(TaskDto dto){
        if (dto == null) return null;
        return Task.builder()
                .id(dto.getId())
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                // apartment, assignedTo y status se setean en el service
                .build();
    }
}
