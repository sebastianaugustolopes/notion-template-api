package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskPriority;
import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponseDTO(
    String id,
    String title,
    String description,
    LocalDate endDate,
    TaskPriority priority,
    TaskStatus status,
    String projectId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime completedAt
) {
    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getEndDate(),
            task.getPriority(),
            task.getStatus(),
            task.getProject().getId(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getCompletedAt()
        );
    }
}
