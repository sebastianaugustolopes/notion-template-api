package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.task.TaskPriority;
import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;

public record TaskRequestDTO(
    String title,
    String description,
    LocalDate endDate,
    TaskPriority priority,
    TaskStatus status
) {}
