package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskPriority;
import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;

public record CalendarTaskDTO(
    String id,
    String title,
    String description,
    LocalDate endDate,
    TaskPriority priority,
    TaskStatus status,
    String projectId,
    String projectName,
    String projectType
) {
    public static CalendarTaskDTO fromEntity(Task task) {
        return new CalendarTaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getEndDate(),
            task.getPriority(),
            task.getStatus(),
            task.getProject().getId(),
            task.getProject().getName(),
            task.getProject().getType().name()
        );
    }
}
