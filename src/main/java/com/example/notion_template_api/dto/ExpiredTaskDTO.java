package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ExpiredTaskDTO(
    String id,
    String title,
    String description,
    LocalDate endDate,
    TaskStatus status,
    String projectId,
    String projectName,
    long daysOverdue
) {
    public static ExpiredTaskDTO fromEntity(Task task) {
        long daysOverdue = ChronoUnit.DAYS.between(task.getEndDate(), LocalDate.now());
        return new ExpiredTaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getEndDate(),
            task.getStatus(),
            task.getProject().getId(),
            task.getProject().getName(),
            daysOverdue
        );
    }
}
