package com.hoteleria.roomsOps.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.hoteleria.roomsOps.model.ChecklistItem;
import com.hoteleria.roomsOps.model.Task;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String titulo;
    private String descripcion;

    @JsonProperty("apartamentoId")
    @JsonAlias("apartmentId")
    private Long apartmentId;

    @JsonProperty("usuarioAsignadoId")
    @JsonAlias("assignedUserId")
    private Long assignedUserId;

    @JsonProperty("estadoId")
    @JsonAlias("statusId")
    private Long statusId;

    @Builder.Default
    @JsonProperty("listaVerificacion")
    @JsonAlias("checklist")
    private List<ChecklistItem> checklist = new ArrayList<>();

    public static TaskDto fromEntity(Task t){
        if (t == null) return null;
        return TaskDto.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .descripcion(t.getDescripcion())
            .apartmentId(t.getApartment() != null ? t.getApartment().getId() : null)
            .assignedUserId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
            .statusId(t.getStatus() != null ? t.getStatus().getId() : null)
            .checklist(t.getChecklist() == null ? new ArrayList<>() : t.getChecklist().stream()
                .map(TaskDto::copyChecklistItem)
                .collect(Collectors.toList()))
                .build();
    }

    public static Task toEntity(TaskDto dto){
        if (dto == null) return null;
        return Task.builder()
                .id(dto.getId())
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .checklist(dto.getChecklist() == null ? new ArrayList<>() : dto.getChecklist().stream()
                        .map(TaskDto::copyChecklistItem)
                        .collect(Collectors.toList()))
                .build();
    }

    private static ChecklistItem copyChecklistItem(ChecklistItem item) {
        if (item == null) {
            return null;
        }

        return ChecklistItem.builder()
                .descripcion(item.getDescripcion())
                .estado(item.getEstado())
                .nota(item.getNota())
                .build();
    }
}

