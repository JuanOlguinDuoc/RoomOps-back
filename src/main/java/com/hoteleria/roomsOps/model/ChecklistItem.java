package com.hoteleria.roomsOps.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private ChecklistStatus estado;

    private String nota;
}
