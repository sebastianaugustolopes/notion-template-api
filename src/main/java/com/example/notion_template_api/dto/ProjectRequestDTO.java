package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.project.Priority;
import com.example.notion_template_api.domain.project.ProjectType;

import java.time.LocalDate;

public record ProjectRequestDTO(
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    ProjectType type,
    Priority priority
) {}
