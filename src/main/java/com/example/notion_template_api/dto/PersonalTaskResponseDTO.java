package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.personal.PersonalTask;
import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PersonalTaskResponseDTO(
        String id,
        String title,
        String description,
        LocalDate endDate,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {

    public static PersonalTaskResponseDTO fromEntity(PersonalTask task) {
        return new PersonalTaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getEndDate(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt()
        );
    }
}

