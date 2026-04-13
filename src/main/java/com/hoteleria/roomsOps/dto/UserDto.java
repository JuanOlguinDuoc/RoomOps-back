package com.hoteleria.roomsOps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoteleria.roomsOps.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String run;
    private String firstName;
    private String lastName;
    private String email;

    //RECORDATORIO: ESTO SOLO RECIBE PERO NO MUESTRA EN LAS RESPUESTAS
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

    public static UserDto fromEntity(User u){
        if (u == null) {
            return null;
        }

        return UserDto.builder()
                .id(u.getId())
                .run(u.getRun())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .password(u.getPassword())
                .role(u.getRole() != null ? u.getRole().getName() : null)
                .build();
    }

    public static User toEntity(UserDto dto){
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .run(dto.getRun())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                // role se asigna en el service
                .build();
    }
}
