

package com.hoteleria.roomsOps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hoteleria.roomsOps.model.Role;
import com.hoteleria.roomsOps.model.User;
import com.hoteleria.roomsOps.repository.RoleRepo;
import com.hoteleria.roomsOps.repository.UserRepo;


@Component
public class Initializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles por defecto si no existen
        createRoleIfNotExists("ADMINISTRADOR");
        createRoleIfNotExists("SUPERVISOR");
        createRoleIfNotExists("TRABAJADOR");
        
        // Crear usuarios por defecto si no existen
        createUserIfNotExists("00000000-0", "admin", "admin", "admin@duoc.cl", "admin123", "ADMINISTRADOR");
        createUserIfNotExists("00000000-1", "supervisor", "supervisor", "supervisor@duoc.cl", "supervisor123", "SUPERVISOR");
        createUserIfNotExists("00000000-2", "trabajador", "trabajador", "trabajador@duoc.cl", "trabajador123", "TRABAJADOR");

        System.out.println("✓ Datos inicializados correctamente");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepo.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepo.save(role);
            System.out.println("✓ Rol creado: " + roleName);
        }
    }

    private void createUserIfNotExists(String run, String firstName, String lastName, String email, String password, String roleName) {
        if (userRepo.findByEmail(email).isEmpty()) {
            Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
            
            User user = new User();
            user.setRun(run);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // Encriptar contraseña
            user.setRole(role);
            userRepo.save(user);
            System.out.println("✓ Usuario creado: " + email + " con rol: " + roleName);
        }
    }
}
