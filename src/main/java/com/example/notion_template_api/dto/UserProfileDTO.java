package com.example.notion_template_api.dto;

import com.example.notion_template_api.domain.user.User;

public record UserProfileDTO(
    String id,
    String name,
    String email,
    String profilePhoto
) {
    public static UserProfileDTO fromEntity(User user) {
        return new UserProfileDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getProfilePhoto()
        );
    }
}
