package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.task.Task;
import com.example.notion_template_api.domain.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectIdOrderByEndDateDesc(String projectId);
    
    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.project.id = :projectId")
    Optional<Task> findByIdAndProjectId(@Param("taskId") String taskId, @Param("projectId") String projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId")
    int countByProjectId(@Param("projectId") String projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    int countByProjectIdAndStatus(@Param("projectId") String projectId, @Param("status") TaskStatus status);

    // User-level statistics
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.user.id = :userId")
    int countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.user.id = :userId AND t.status = :status")
    int countByUserIdAndStatus(@Param("userId") String userId, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.user.id = :userId AND t.endDate < :today AND t.status <> 'DONE'")
    int countMissedByUserId(@Param("userId") String userId, @Param("today") LocalDate today);

    // Expired tasks by project
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.endDate < :today AND t.status <> 'DONE' ORDER BY t.endDate ASC")
    List<Task> findExpiredByProjectId(@Param("projectId") String projectId, @Param("today") LocalDate today);

    // Count expired tasks by project
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.endDate < :today AND t.status <> 'DONE'")
    int countExpiredByProjectId(@Param("projectId") String projectId, @Param("today") LocalDate today);

    // All expired tasks for a user (across all projects)
    @Query("SELECT t FROM Task t WHERE t.project.user.id = :userId AND t.endDate < :today AND t.status <> 'DONE' ORDER BY t.endDate ASC")
    List<Task> findExpiredByUserId(@Param("userId") String userId, @Param("today") LocalDate today);

    // All tasks for a user (for calendar view)
    @Query("SELECT t FROM Task t JOIN FETCH t.project WHERE t.project.user.id = :userId ORDER BY t.endDate ASC")
    List<Task> findAllByUserId(@Param("userId") String userId);

    // Tasks for a user within a date range (for calendar view optimization)
    @Query("SELECT t FROM Task t JOIN FETCH t.project WHERE t.project.user.id = :userId AND t.endDate >= :startDate AND t.endDate <= :endDate ORDER BY t.endDate ASC")
    List<Task> findByUserIdAndDateRange(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
