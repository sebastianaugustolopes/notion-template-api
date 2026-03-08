package com.example.notion_template_api.domain.personal;

import com.example.notion_template_api.domain.task.TaskStatus;
import com.example.notion_template_api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.TODO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == TaskStatus.DONE;
    }

    public void setStatus(TaskStatus newStatus) {
        TaskStatus oldStatus = this.status;
        this.status = newStatus;

        if (newStatus == TaskStatus.DONE && oldStatus != TaskStatus.DONE) {
            this.completedAt = LocalDateTime.now();
        } else if (newStatus != TaskStatus.DONE) {
            this.completedAt = null;
        }
    }
}

