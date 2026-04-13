package com.hoteleria.roomsOps.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoteleria.roomsOps.dto.TaskDto;
import com.hoteleria.roomsOps.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TaskService service;

    @Test
    void listTasksOk() throws Exception {
        when(service.getAllTasks()).thenReturn(List.of(
                TaskDto.builder().id(1L).titulo("T1").descripcion("D1").build(),
                TaskDto.builder().id(2L).titulo("T2").descripcion("D2").build()));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("T1"))
                .andExpect(jsonPath("$[1].titulo").value("T2"));
    }

    @Test
    void createTaskCreated() throws Exception {
        TaskDto request = TaskDto.builder()
                .titulo("Tarea nueva")
                .descripcion("Detalle")
                .apartmentId(1L)
                .statusId(2L)
                .build();

        TaskDto created = TaskDto.builder()
                .id(10L)
                .titulo("Tarea nueva")
                .descripcion("Detalle")
                .apartmentId(1L)
                .statusId(2L)
                .build();

        when(service.createTask(any(TaskDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Tarea generada correctamente"))
                .andExpect(jsonPath("$.tarea.id").value(10))
                .andExpect(jsonPath("$.tarea.titulo").value("Tarea nueva"));
    }

    @Test
    void createTaskBadRequest() throws Exception {
        TaskDto request = TaskDto.builder().titulo("X").build();

        when(service.createTask(any(TaskDto.class))).thenThrow(new IllegalArgumentException("Datos invalidos"));

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error al crear tarea"))
                .andExpect(jsonPath("$.error").value("Datos invalidos"));
    }

    @Test
    void getTaskOk() throws Exception {
        when(service.getTaskById(5L)).thenReturn(TaskDto.builder().id(5L).titulo("T5").build());

        mockMvc.perform(get("/api/v1/tasks/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.titulo").value("T5"));
    }

    @Test
    void getTaskNotFound() throws Exception {
        when(service.getTaskById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Tarea no encontrada"));
    }

    @Test
    void updateTaskOk() throws Exception {
        TaskDto request = TaskDto.builder().titulo("Nueva").descripcion("Desc").build();
        TaskDto updated = TaskDto.builder().id(7L).titulo("Nueva").descripcion("Desc").build();

        when(service.updateTask(eq(7L), any(TaskDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/tasks/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Tarea actualizada"))
                .andExpect(jsonPath("$.tarea.id").value(7))
                .andExpect(jsonPath("$.tarea.titulo").value("Nueva"));
    }

    @Test
    void updateTaskNotFound() throws Exception {
        TaskDto request = TaskDto.builder().titulo("Nueva").build();
        when(service.updateTask(eq(8L), any(TaskDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/tasks/8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Tarea no encontrada"));
    }

    @Test
    void updateTaskBadRequest() throws Exception {
        TaskDto request = TaskDto.builder().titulo("Nueva").build();
        when(service.updateTask(eq(7L), any(TaskDto.class))).thenThrow(new IllegalArgumentException("Error update"));

        mockMvc.perform(put("/api/v1/tasks/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error al actualizar tarea"))
                .andExpect(jsonPath("$.error").value("Error update"));
    }

    @Test
    void deleteTaskOk() throws Exception {
        doNothing().when(service).deleteTask(3L);

        mockMvc.perform(delete("/api/v1/tasks/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Tarea eliminada"));
    }

    @Test
    void deleteTaskBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("No se puede")).when(service).deleteTask(3L);

        mockMvc.perform(delete("/api/v1/tasks/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error al eliminar tarea"))
                .andExpect(jsonPath("$.error").value("No se puede"));
    }
}
