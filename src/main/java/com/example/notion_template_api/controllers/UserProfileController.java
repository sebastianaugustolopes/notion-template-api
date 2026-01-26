package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.UpdateProfileDTO;
import com.example.notion_template_api.dto.UserProfileDTO;
import com.example.notion_template_api.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserProfileDTO.fromEntity(user));
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileDTO dto) {
        
        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }
        
        if (dto.email() != null && !dto.email().isBlank()) {
            // Check if email is already in use by another user
            var existingUser = userRepository.findByEmail(dto.email());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }
            user.setEmail(dto.email());
        }
        
        if (dto.profilePhoto() != null) {
            user.setProfilePhoto(dto.profilePhoto());
        }
        
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserProfileDTO.fromEntity(updatedUser));
    }
}
