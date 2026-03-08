package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.personal.PersonalTask;
import com.example.notion_template_api.domain.task.TaskStatus;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.PersonalTaskRequestDTO;
import com.example.notion_template_api.dto.PersonalTaskResponseDTO;
import com.example.notion_template_api.repositories.PersonalTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/personal-tasks")
@RequiredArgsConstructor
public class PersonalTaskController {

    private final PersonalTaskRepository personalTaskRepository;

    @PostMapping
    public ResponseEntity<PersonalTaskResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestBody PersonalTaskRequestDTO request
    ) {
        if (request.title() == null || request.title().isBlank() || request.endDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        PersonalTask task = new PersonalTask();
        task.setUser(user);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setEndDate(request.endDate());
        task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);

        PersonalTask saved = personalTaskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(PersonalTaskResponseDTO.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<PersonalTaskResponseDTO>> getAll(@AuthenticationPrincipal User user) {
        List<PersonalTask> tasks = personalTaskRepository.findAllByUserId(user.getId());
        List<PersonalTaskResponseDTO> result = tasks.stream()
                .map(PersonalTaskResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalTaskResponseDTO> getById(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        return personalTaskRepository.findByIdAndUserId(id, user.getId())
                .map(task -> ResponseEntity.ok(PersonalTaskResponseDTO.fromEntity(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/range")
    public ResponseEntity<List<PersonalTaskResponseDTO>> getByRange(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        List<PersonalTask> tasks = personalTaskRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
        List<PersonalTaskResponseDTO> result = tasks.stream()
                .map(PersonalTaskResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalTaskResponseDTO> update(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody PersonalTaskRequestDTO request
    ) {
        return personalTaskRepository.findByIdAndUserId(id, user.getId())
                .map(task -> {
                    if (request.title() != null) {
                        task.setTitle(request.title());
                    }
                    task.setDescription(request.description());
                    if (request.endDate() != null) {
                        task.setEndDate(request.endDate());
                    }
                    if (request.status() != null) {
                        task.setStatus(request.status());
                    }
                    PersonalTask updated = personalTaskRepository.save(task);
                    return ResponseEntity.ok(PersonalTaskResponseDTO.fromEntity(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PersonalTaskResponseDTO> updateStatus(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request
    ) {
        if (request.status() == null) {
            return ResponseEntity.badRequest().build();
        }

        return personalTaskRepository.findByIdAndUserId(id, user.getId())
                .map(task -> {
                    task.setStatus(request.status());
                    PersonalTask updated = personalTaskRepository.save(task);
                    return ResponseEntity.ok(PersonalTaskResponseDTO.fromEntity(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        return personalTaskRepository.findByIdAndUserId(id, user.getId())
                .map(task -> {
                    personalTaskRepository.delete(task);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public record StatusUpdateRequest(TaskStatus status) {}
}

