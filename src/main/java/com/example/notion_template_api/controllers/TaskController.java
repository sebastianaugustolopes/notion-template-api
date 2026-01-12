package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskStatus;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.TaskRequestDTO;
import com.example.notion_template_api.dto.TaskResponseDTO;
import com.example.notion_template_api.repositories.ProjectRepository;
import com.example.notion_template_api.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId,
            @RequestBody TaskRequestDTO request) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .map(project -> {
                    if (request.endDate().isAfter(project.getEndDate())) {
                        return ResponseEntity.badRequest().<TaskResponseDTO>build();
                    }

                    Task task = new Task();
                    task.setTitle(request.title());
                    task.setDescription(request.description());
                    task.setEndDate(request.endDate());
                    task.setPriority(request.priority());
                    task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);
                    task.setProject(project);

                    Task savedTask = taskRepository.save(task);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(TaskResponseDTO.fromEntity(savedTask));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProjectIdOrderByEndDateDesc(projectId);
                    List<TaskResponseDTO> response = tasks.stream()
                            .map(TaskResponseDTO::fromEntity)
                            .toList();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTask(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId,
            @PathVariable String taskId) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .flatMap(project -> taskRepository.findByIdAndProjectId(taskId, projectId))
                .map(task -> ResponseEntity.ok(TaskResponseDTO.fromEntity(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId,
            @PathVariable String taskId,
            @RequestBody TaskRequestDTO request) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .flatMap(project -> taskRepository.findByIdAndProjectId(taskId, projectId)
                        .map(task -> {
                            if (request.endDate().isAfter(project.getEndDate())) {
                                return ResponseEntity.badRequest().<TaskResponseDTO>build();
                            }

                            task.setTitle(request.title());
                            task.setDescription(request.description());
                            task.setEndDate(request.endDate());
                            task.setPriority(request.priority());
                            if (request.status() != null) {
                                task.setStatus(request.status());
                            }

                            Task updatedTask = taskRepository.save(task);
                            return ResponseEntity.ok(TaskResponseDTO.fromEntity(updatedTask));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId,
            @PathVariable String taskId,
            @RequestBody TaskStatusUpdateRequest request) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .flatMap(project -> taskRepository.findByIdAndProjectId(taskId, projectId)
                        .map(task -> {
                            task.setStatus(request.status());
                            Task updatedTask = taskRepository.save(task);
                            return ResponseEntity.ok(TaskResponseDTO.fromEntity(updatedTask));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId,
            @PathVariable String taskId) {

        return projectRepository.findById(projectId)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .flatMap(project -> taskRepository.findByIdAndProjectId(taskId, projectId)
                        .map(task -> {
                            taskRepository.delete(task);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    public record TaskStatusUpdateRequest(TaskStatus status) {}
}
