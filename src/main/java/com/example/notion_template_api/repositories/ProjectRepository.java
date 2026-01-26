package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.project.Project;
import com.example.notion_template_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByUserOrderByCreatedAtDesc(User user);
    List<Project> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Project> findByIdAndUserId(String id, String userId);
}
