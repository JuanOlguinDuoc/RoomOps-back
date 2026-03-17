package com.hoteleria.roomsOps.dto;

import com.hoteleria.roomsOps.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String name;

    public static RoleDto fromEntity(Role r){
    if (r == null) return null;
    return RoleDto.builder()
            .id(r.getId())
            .name(r.getName())
            .build();
    }

    public static Role toEntity(RoleDto dto){
        if (dto == null) return null;
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
