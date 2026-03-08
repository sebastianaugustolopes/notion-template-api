package com.example.notion_template_api.repositories;

import com.example.notion_template_api.domain.personal.PersonalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalTaskRepository extends JpaRepository<PersonalTask, String> {

    @Query("SELECT p FROM PersonalTask p WHERE p.user.id = :userId ORDER BY p.endDate ASC")
    List<PersonalTask> findAllByUserId(@Param("userId") String userId);

    @Query("""
        SELECT p FROM PersonalTask p
        WHERE p.user.id = :userId
          AND p.endDate >= :startDate
          AND p.endDate <= :endDate
        ORDER BY p.endDate ASC
        """)
    List<PersonalTask> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM PersonalTask p WHERE p.id = :id AND p.user.id = :userId")
    Optional<PersonalTask> findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
}

