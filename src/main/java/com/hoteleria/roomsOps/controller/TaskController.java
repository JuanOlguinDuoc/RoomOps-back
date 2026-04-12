package com.hoteleria.roomsOps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoteleria.roomsOps.dto.TaskDto;
import com.hoteleria.roomsOps.service.TaskService;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@GetMapping
	public List<TaskDto> listTasks() {
		return taskService.getAllTasks();
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> createTask(@RequestBody TaskDto dto) {
		Map<String, Object> response = new HashMap<>();
		try {
			TaskDto created = taskService.createTask(dto);
			response.put("mensaje", "Tarea generada correctamente");
			response.put("tarea", created);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			response.put("mensaje", "Error al crear tarea");
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getTask(@PathVariable Long id) {
		TaskDto dto = taskService.getTaskById(id);
		if (dto == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(Map.of("mensaje", "Tarea no encontrada"));
		}
		return ResponseEntity.ok(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateTask(@PathVariable Long id, @RequestBody TaskDto dto) {
		try {
			TaskDto updated = taskService.updateTask(id, dto);
			if (updated == null) {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body(Map.of("mensaje", "Tarea no encontrada"));
			}
			return ResponseEntity.ok(Map.of("mensaje", "Tarea actualizada", "tarea", updated));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("mensaje", "Error al actualizar tarea", "error", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
		try {
			taskService.deleteTask(id);
			return ResponseEntity.ok(Map.of("mensaje", "Tarea eliminada"));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("mensaje", "Error al eliminar tarea", "error", e.getMessage()));
		}
	}
}
