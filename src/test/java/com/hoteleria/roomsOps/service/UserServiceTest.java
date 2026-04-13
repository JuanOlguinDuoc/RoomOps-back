package com.hoteleria.roomsOps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hoteleria.roomsOps.dto.UserDto;
import com.hoteleria.roomsOps.model.Role;
import com.hoteleria.roomsOps.model.User;
import com.hoteleria.roomsOps.repository.RoleRepo;
import com.hoteleria.roomsOps.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private UserService service;

    @Test
    void getUsers() {
        Role role = Role.builder().id(1L).name("trabajador").build();
        User user = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("prueba")
                .role(role)
                .build();

        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserDto> result = service.getUsers();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        assertEquals("trabajador", result.get(0).getRole());
    }

    @Test
    void createUserSuccess() {
        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("prueba")
                .role("trabajador")
                .build();

        Role role = Role.builder().id(1L).name("trabajador").build();
        User saved = User.builder()
                .id(1L)
                .run(dto.getRun())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(role)
                .build();

        when(userRepo.existsByEmail("juan@example.com")).thenReturn(false);
        when(userRepo.existsByRun("12345678-9")).thenReturn(false);
        when(roleRepo.findByName("trabajador")).thenReturn(Optional.of(role));
        when(userRepo.save(any(User.class))).thenReturn(saved);

        UserDto result = service.createUser(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("juan@example.com", result.getEmail());
        assertEquals("trabajador", result.getRole());
    }

    @Test
    void createUserFailsWhenRoleMissing() {
        UserDto dto = UserDto.builder()
                .email("juan@example.com")
                .password("prueba")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto));

        assertEquals("role is required", ex.getMessage());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createUserFailsWhenEmailDuplicate() {
        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .email("juan@example.com")
                .password("prueba")
                .role("trabajador")
                .build();

        when(userRepo.existsByEmail("juan@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto));

        assertTrue(ex.getMessage().contains("correo ya está registrado"));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createUserFailsWhenRunDuplicate() {
        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .email("juan@example.com")
                .password("prueba")
                .role("trabajador")
                .build();

        when(userRepo.existsByEmail("juan@example.com")).thenReturn(false);
        when(userRepo.existsByRun("12345678-9")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto));

        assertTrue(ex.getMessage().contains("RUN ya está registrado"));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createUserFailsWhenRoleNotFound() {
        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .email("juan@example.com")
                .password("prueba")
                .role("inexistente")
                .build();

        when(userRepo.existsByEmail("juan@example.com")).thenReturn(false);
        when(userRepo.existsByRun("12345678-9")).thenReturn(false);
        when(roleRepo.findByName("inexistente")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto));

        assertTrue(ex.getMessage().contains("Role not found"));
        verify(userRepo, never()).save(any(User.class));
    }

        @Test
        void createUserWithNullRunSkipsRunDuplicateCheck() {
                UserDto dto = UserDto.builder()
                                .run(null)
                                .firstName("Juan")
                                .lastName("Olguin")
                                .email("juan@example.com")
                                .password("prueba")
                                .role("trabajador")
                                .build();

                Role role = Role.builder().id(1L).name("trabajador").build();
                User saved = User.builder()
                                .id(1L)
                                .run(null)
                                .firstName("Juan")
                                .lastName("Olguin")
                                .email("juan@example.com")
                                .password("prueba")
                                .role(role)
                                .build();

                when(userRepo.existsByEmail("juan@example.com")).thenReturn(false);
                when(roleRepo.findByName("trabajador")).thenReturn(Optional.of(role));
                when(userRepo.save(any(User.class))).thenReturn(saved);

                UserDto result = service.createUser(dto);

                assertNotNull(result);
                assertEquals("juan@example.com", result.getEmail());
                verify(userRepo, never()).existsByRun(any(String.class));
        }

    @Test
    void findByIdFound() {
        Role role = Role.builder().id(1L).name("trabajador").build();
        User user = User.builder().id(7L).run("11111111-1").firstName("Ana").lastName("Perez")
                .email("ana@example.com").password("clave").role(role).build();

        when(userRepo.findById(7L)).thenReturn(Optional.of(user));

        UserDto result = service.findById(7L);

        assertNotNull(result);
        assertEquals(7L, result.getId());
        assertEquals("ana@example.com", result.getEmail());
    }

    @Test
    void findByIdMissing() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        UserDto result = service.findById(99L);

        assertNull(result);
    }

    @Test
    void findByEmailFound() {
        Role role = Role.builder().id(1L).name("admin").build();
        User user = User.builder().id(8L).run("22222222-2").firstName("Maria").lastName("Lopez")
                .email("maria@example.com").password("clave").role(role).build();

        when(userRepo.findByEmail("maria@example.com")).thenReturn(Optional.of(user));

        UserDto result = service.findByEmail("maria@example.com");

        assertNotNull(result);
        assertEquals(8L, result.getId());
        assertEquals("admin", result.getRole());
    }

    @Test
    void findByEmailMissing() {
        when(userRepo.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        UserDto result = service.findByEmail("noexiste@example.com");

        assertNull(result);
    }

    @Test
    void updateUserSuccess() {
        Role oldRole = Role.builder().id(1L).name("trabajador").build();
        Role newRole = Role.builder().id(2L).name("admin").build();

        User existing = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("vieja")
                .role(oldRole)
                .build();

        UserDto dto = UserDto.builder()
                .run("99999999-9")
                .firstName("Juanito")
                .lastName("Olguin")
                .email("juanito@example.com")
                .password("nueva")
                .role("admin")
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepo.findByName("admin")).thenReturn(Optional.of(newRole));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = service.updateUser(1L, dto);

        assertEquals("99999999-9", result.getRun());
        assertEquals("Juanito", result.getFirstName());
        assertEquals("juanito@example.com", result.getEmail());
        assertEquals("admin", result.getRole());
    }

    @Test
    void updateUserFailsWhenMissing() {
        when(userRepo.findById(5L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(5L, UserDto.builder().build()));

        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void updateUserSkipsPasswordAndRoleWhenNotProvided() {
        Role oldRole = Role.builder().id(1L).name("trabajador").build();

        User existing = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("vieja")
                .role(oldRole)
                .build();

        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("")
                .role(null)
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = service.updateUser(1L, dto);

        assertEquals("vieja", result.getPassword());
        assertEquals("trabajador", result.getRole());
        verify(roleRepo, never()).findByName(any(String.class));
    }

    @Test
    void updateUserFailsWhenRoleNotFound() {
        Role oldRole = Role.builder().id(1L).name("trabajador").build();

        User existing = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("vieja")
                .role(oldRole)
                .build();

        UserDto dto = UserDto.builder()
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .role("inexistente")
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepo.findByName("inexistente")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(1L, dto));

        assertTrue(ex.getMessage().contains("Role not found"));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deleteUserSuccess() {
        when(userRepo.existsById(10L)).thenReturn(true);

        service.deleteUser(10L);

        verify(userRepo).deleteById(10L);
    }

    @Test
    void deleteUserFailsWhenMissing() {
        when(userRepo.existsById(11L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteUser(11L));

        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepo, never()).deleteById(11L);
    }

    @Test
    void patchUserSuccess() {
        Role role = Role.builder().id(1L).name("trabajador").build();
        User existing = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("vieja")
                .role(role)
                .build();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Pedro");
        updates.put("lastName", "Gomez");
        updates.put("email", "pedro@example.com");
        updates.put("password", "nueva");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = service.patchUser(1L, updates);

        assertEquals("Pedro", result.getFirstName());
        assertEquals("Gomez", result.getLastName());
        assertEquals("pedro@example.com", result.getEmail());
        assertEquals("nueva", result.getPassword());
    }

    @Test
    void patchUserDoesNotOverrideWithEmptyPassword() {
        Role role = Role.builder().id(1L).name("trabajador").build();
        User existing = User.builder()
                .id(1L)
                .run("12345678-9")
                .firstName("Juan")
                .lastName("Olguin")
                .email("juan@example.com")
                .password("vieja")
                .role(role)
                .build();

        Map<String, Object> updates = new HashMap<>();
        updates.put("password", "");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = service.patchUser(1L, updates);

        assertEquals("vieja", result.getPassword());
    }

        @Test
        void patchUserWithoutPasswordFieldKeepsCurrentPassword() {
                Role role = Role.builder().id(1L).name("trabajador").build();
                User existing = User.builder()
                                .id(1L)
                                .run("12345678-9")
                                .firstName("Juan")
                                .lastName("Olguin")
                                .email("juan@example.com")
                                .password("vieja")
                                .role(role)
                                .build();

                Map<String, Object> updates = Map.of("firstName", "Pedro");

                when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
                when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

                UserDto result = service.patchUser(1L, updates);

                assertEquals("Pedro", result.getFirstName());
                assertEquals("vieja", result.getPassword());
        }

        @Test
        void patchUserDoesNotOverrideWithNullPassword() {
                Role role = Role.builder().id(1L).name("trabajador").build();
                User existing = User.builder()
                                .id(1L)
                                .run("12345678-9")
                                .firstName("Juan")
                                .lastName("Olguin")
                                .email("juan@example.com")
                                .password("vieja")
                                .role(role)
                                .build();

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", null);

                when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
                when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

                UserDto result = service.patchUser(1L, updates);

                assertEquals("vieja", result.getPassword());
        }

    @Test
    void patchUserFailsWhenMissing() {
        when(userRepo.findById(42L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.patchUser(42L, Map.of("firstName", "Pedro")));

        assertTrue(ex.getMessage().contains("User not found"));
    }
}
