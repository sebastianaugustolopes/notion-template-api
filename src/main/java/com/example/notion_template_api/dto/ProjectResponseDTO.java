package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.project.Priority;
import com.example.notion_template_api.domain.project.Project;
import com.example.notion_template_api.domain.project.ProjectType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponseDTO(
    String id,
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    ProjectType type,
    Priority priority,
    int totalTasks,
    int completedTasks,
    double progressPercentage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProjectResponseDTO fromEntity(Project project) {
        return new ProjectResponseDTO(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStartDate(),
            project.getEndDate(),
            project.getType(),
            project.getPriority(),
            project.getTotalTasks(),
            project.getCompletedTasks(),
            project.getProgressPercentage(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
