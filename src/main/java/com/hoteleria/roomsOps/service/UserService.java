package com.hoteleria.roomsOps.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoteleria.roomsOps.dto.UserDto;
import com.hoteleria.roomsOps.model.Role;
import com.hoteleria.roomsOps.model.User;
import com.hoteleria.roomsOps.repository.RoleRepo;
import com.hoteleria.roomsOps.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    public List<User> getUsers(){
        return userRepo.findAll();
    }

    public UserDto createUser(UserDto dto) {
        User entity = UserDto.toEntity(dto);

        if (dto.getRole() == null) {
            throw new IllegalArgumentException("role is required");
        }

        // check duplicates
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        if (dto.getRun() != null && userRepo.existsByRun(dto.getRun())) {
            throw new IllegalArgumentException("El RUN ya está registrado");
        }

        entity.setPassword(dto.getPassword());

        Role role = roleRepo.findByName(dto.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRole()));

        entity.setRole(role);

        User saved = userRepo.save(entity);
        return UserDto.fromEntity(saved);
    }

    public UserDto findById(Long id){
        return userRepo.findById(id)
                .map(UserDto::fromEntity)
                .orElse(null);
    }

    public UserDto findByEmail(String email){
        return userRepo.findByEmail(email)
                .map(UserDto::fromEntity)
                .orElse(null);
    }

    public UserDto updateUser(Long id, UserDto dto){
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existing.setRun(dto.getRun());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());

        // ❌ SIN encriptación
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(dto.getPassword());
        }

        if (dto.getRole() != null) {
            Role role = roleRepo.findByName(dto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRole()));
            existing.setRole(role);
        }

        User saved = userRepo.save(existing);
        return UserDto.fromEntity(saved);
    }

    public void deleteUser(Long id){
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepo.deleteById(id);
    }

    public UserDto patchUser(Long id, Map<String, Object> updates){
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updates.containsKey("firstName"))
            existing.setFirstName((String) updates.get("firstName"));

        if (updates.containsKey("lastName"))
            existing.setLastName((String) updates.get("lastName"));

        if (updates.containsKey("email"))
            existing.setEmail((String) updates.get("email"));

        // ❌ SIN encriptación
        if (updates.containsKey("password")) {
            String password = (String) updates.get("password");
            if (password != null && !password.isEmpty()) {
                existing.setPassword(password);
            }
        }

        User saved = userRepo.save(existing);
        return UserDto.fromEntity(saved);
    }
}
