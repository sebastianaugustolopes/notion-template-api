package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskStatus;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.ExpiredTaskDTO;
import com.example.notion_template_api.dto.TaskStatsDTO;
import com.example.notion_template_api.repositories.ProjectRepository;
import com.example.notion_template_api.repositories.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user/stats")
public class UserStatsController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public UserStatsController(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/tasks")
    public ResponseEntity<TaskStatsDTO> getTaskStats(@AuthenticationPrincipal User user) {
        String userId = user.getId();
        LocalDate today = LocalDate.now();

        int totalTasks = taskRepository.countByUserId(userId);
        int todoCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.TODO);
        int planningCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PLANNING);
        int inProgressCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        int doneCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE);
        int missedCount = taskRepository.countMissedByUserId(userId, today);

        TaskStatsDTO stats = new TaskStatsDTO(
            totalTasks,
            todoCount,
            planningCount,
            inProgressCount,
            doneCount,
            missedCount
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/expired-tasks")
    public ResponseEntity<List<ExpiredTaskDTO>> getExpiredTasks(@AuthenticationPrincipal User user) {
        String userId = user.getId();
        LocalDate today = LocalDate.now();

        List<Task> expiredTasks = taskRepository.findExpiredByUserId(userId, today);
        List<ExpiredTaskDTO> result = expiredTasks.stream()
            .map(ExpiredTaskDTO::fromEntity)
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectId}/expired-tasks")
    public ResponseEntity<List<ExpiredTaskDTO>> getExpiredTasksByProject(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId) {
        
        // Verify project belongs to user
        var project = projectRepository.findByIdAndUserId(projectId, user.getId());
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LocalDate today = LocalDate.now();
        List<Task> expiredTasks = taskRepository.findExpiredByProjectId(projectId, today);
        List<ExpiredTaskDTO> result = expiredTasks.stream()
            .map(ExpiredTaskDTO::fromEntity)
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectId}/expired-count")
    public ResponseEntity<Integer> getExpiredTasksCount(
            @AuthenticationPrincipal User user,
            @PathVariable String projectId) {
        
        // Verify project belongs to user
        var project = projectRepository.findByIdAndUserId(projectId, user.getId());
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LocalDate today = LocalDate.now();
        int count = taskRepository.countExpiredByProjectId(projectId, today);

        return ResponseEntity.ok(count);
    }
}
