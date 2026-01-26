package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.CalendarTaskDTO;
import com.example.notion_template_api.repositories.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final TaskRepository taskRepository;

    public CalendarController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<CalendarTaskDTO>> getAllTasks(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskRepository.findAllByUserId(user.getId());
        List<CalendarTaskDTO> result = tasks.stream()
            .map(CalendarTaskDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/range")
    public ResponseEntity<List<CalendarTaskDTO>> getTasksByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        List<Task> tasks = taskRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
        List<CalendarTaskDTO> result = tasks.stream()
            .map(CalendarTaskDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(result);
    }
}
