package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.task.TaskStatus;

import java.time.LocalDate;

public record PersonalTaskRequestDTO(
        String title,
        String description,
        LocalDate endDate,
        TaskStatus status
) {}

