package org.example.unihub.controller.assessment.task;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TaskDTO;
import org.example.unihub.dto.TaskRequest;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TaskDTO> createTask(
            @PathVariable Long laboratoryId,
            @RequestBody TaskRequest taskRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskDTO createdTask = taskService.createTask(laboratoryId, taskRequest, userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<TaskDTO>> getTasksByLaboratoryId(@PathVariable Long laboratoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TaskDTO> tasks = taskService.getTasksByLaboratoryId(laboratoryId, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskDTO task = taskService.getTaskById(taskId, userId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskDTO taskDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
} 