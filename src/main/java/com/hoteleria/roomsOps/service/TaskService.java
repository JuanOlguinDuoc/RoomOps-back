package com.hoteleria.roomsOps.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoteleria.roomsOps.model.Task;
import com.hoteleria.roomsOps.repository.TaskRepo;
import com.hoteleria.roomsOps.dto.TaskDto;

@Service
public class TaskService {
    @Autowired
    private TaskRepo taskRepo;

    public List<TaskDto> getAllTasks() {
        return taskRepo.findAll().stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        return taskRepo.findById(id)
                .map(TaskDto::fromEntity)
                .orElse(null);
    }

    public TaskDto createTask(TaskDto taskDto) {
        Task task = TaskDto.toEntity(taskDto);
        task = taskRepo.save(task);
        return TaskDto.fromEntity(task);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        return taskRepo.findById(id)
                .map(existingTask -> {
                    existingTask.setTitulo(taskDto.getTitulo());
                    existingTask.setDescripcion(taskDto.getDescripcion());
                    // Update other fields as needed
                    existingTask = taskRepo.save(existingTask);
                    return TaskDto.fromEntity(existingTask);
                })
                .orElse(null);
    }

    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }
}
