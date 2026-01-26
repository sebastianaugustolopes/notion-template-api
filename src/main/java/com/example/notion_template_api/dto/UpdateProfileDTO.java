package com.example.notion_template_api.dto;

public record UpdateProfileDTO(
    String name,
    String email,
    String profilePhoto
) {
}
