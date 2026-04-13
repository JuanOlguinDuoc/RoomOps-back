package com.hoteleria.roomsOps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hoteleria.roomsOps.dto.ApartmentDto;
import com.hoteleria.roomsOps.model.Apartment;
import com.hoteleria.roomsOps.repository.ApartmentRepo;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {

    @Mock
    private ApartmentRepo repo;

    @InjectMocks
    private ApartmentService service;

    @Test
    void getApartments() {
        Apartment apartment = Apartment.builder().id(1L).nombre("A-101").activo(true).build();
        when(repo.findAll()).thenReturn(List.of(apartment));

        List<ApartmentDto> result = service.getApartments();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("A-101", result.get(0).getNombre());
        assertEquals(true, result.get(0).getActivo());
    }

    @Test
    void createApartment() {
        ApartmentDto dto = ApartmentDto.builder().nombre("A-101").activo(true).build();
        Apartment saved = Apartment.builder().id(10L).nombre("A-101").activo(true).build();
        when(repo.save(any(Apartment.class))).thenReturn(saved);

        ApartmentDto result = service.createApartment(dto);

        assertEquals(10L, result.getId());
        assertEquals("A-101", result.getNombre());
        assertEquals(true, result.getActivo());
    }

    @Test
    void findByIdFound() {
        Apartment apartment = Apartment.builder().id(2L).nombre("B-202").activo(false).build();
        when(repo.findById(2L)).thenReturn(Optional.of(apartment));

        ApartmentDto result = service.findById(2L);

        assertEquals(2L, result.getId());
        assertEquals("B-202", result.getNombre());
        assertEquals(false, result.getActivo());
    }

    @Test
    void findByIdMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        ApartmentDto result = service.findById(99L);

        assertNull(result);
    }

    @Test
    void updateApartment() {
        Apartment existing = Apartment.builder().id(3L).nombre("C-303").activo(true).build();
        ApartmentDto dto = ApartmentDto.builder().nombre("C-303-NEW").activo(false).build();

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Apartment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApartmentDto result = service.updateApartment(3L, dto);

        assertEquals(3L, result.getId());
        assertEquals("C-303-NEW", result.getNombre());
        assertEquals(false, result.getActivo());
    }

    @Test
    void updateApartmentMissing() {
        ApartmentDto dto = ApartmentDto.builder().nombre("X").activo(true).build();
        when(repo.findById(77L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateApartment(77L, dto));

        assertTrue(ex.getMessage().contains("Apartamento no encontrado"));
        verify(repo, never()).save(any(Apartment.class));
    }

    @Test
    void updateEstado() {
        Apartment existing = Apartment.builder().id(4L).nombre("D-404").activo(true).build();

        when(repo.findById(4L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Apartment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApartmentDto result = service.updateEstado(4L, false);

        assertEquals(4L, result.getId());
        assertEquals("D-404", result.getNombre());
        assertEquals(false, result.getActivo());
    }

    @Test
    void updateEstadoMissing() {
        when(repo.findById(88L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateEstado(88L, true));

        assertTrue(ex.getMessage().contains("Apartamento no encontrado"));
        verify(repo, never()).save(any(Apartment.class));
    }
}
