package com.example.notion_template_api.controllers;

import com.example.notion_template_api.domain.project.Project;
import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.dto.ProjectRequestDTO;
import com.example.notion_template_api.dto.ProjectResponseDTO;
import com.example.notion_template_api.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectRepository projectRepository;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @AuthenticationPrincipal User user,
            @RequestBody ProjectRequestDTO request) {
        
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setType(request.type());
        project.setPriority(request.priority());
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponseDTO.fromEntity(savedProject));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(@AuthenticationPrincipal User user) {
        List<Project> projects = projectRepository.findByUserOrderByCreatedAtDesc(user);
        List<ProjectResponseDTO> response = projects.stream()
                .map(ProjectResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {
        
        return projectRepository.findById(id)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .map(project -> ResponseEntity.ok(ProjectResponseDTO.fromEntity(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody ProjectRequestDTO request) {
        
        return projectRepository.findById(id)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .map(project -> {
                    project.setName(request.name());
                    project.setDescription(request.description());
                    project.setStartDate(request.startDate());
                    project.setEndDate(request.endDate());
                    project.setType(request.type());
                    project.setPriority(request.priority());
                    
                    Project updatedProject = projectRepository.save(project);
                    return ResponseEntity.ok(ProjectResponseDTO.fromEntity(updatedProject));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {
        
        return projectRepository.findById(id)
                .filter(project -> project.getUser().getId().equals(user.getId()))
                .map(project -> {
                    projectRepository.delete(project);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
