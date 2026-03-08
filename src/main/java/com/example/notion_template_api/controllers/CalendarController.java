package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.personal.PersonalTask;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.CalendarTaskDTO;
import com.example.notion_template_api.repositories.TaskRepository;
import com.example.notion_template_api.repositories.PersonalTaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final TaskRepository taskRepository;
    private final PersonalTaskRepository personalTaskRepository;

    public CalendarController(TaskRepository taskRepository, PersonalTaskRepository personalTaskRepository) {
        this.taskRepository = taskRepository;
        this.personalTaskRepository = personalTaskRepository;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<CalendarTaskDTO>> getAllTasks(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskRepository.findAllByUserId(user.getId());
        List<PersonalTask> personalTasks = personalTaskRepository.findAllByUserId(user.getId());

        List<CalendarTaskDTO> result = new ArrayList<>();
        result.addAll(tasks.stream().map(CalendarTaskDTO::fromEntity).toList());
        result.addAll(personalTasks.stream().map(CalendarTaskDTO::fromPersonalTask).toList());

        result.sort(Comparator.comparing(CalendarTaskDTO::endDate));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/range")
    public ResponseEntity<List<CalendarTaskDTO>> getTasksByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<Task> tasks = taskRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
        List<PersonalTask> personalTasks = personalTaskRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);

        List<CalendarTaskDTO> result = new ArrayList<>();
        result.addAll(tasks.stream().map(CalendarTaskDTO::fromEntity).toList());
        result.addAll(personalTasks.stream().map(CalendarTaskDTO::fromPersonalTask).toList());

        result.sort(Comparator.comparing(CalendarTaskDTO::endDate));
        return ResponseEntity.ok(result);
    }
}
