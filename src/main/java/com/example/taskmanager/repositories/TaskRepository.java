package com.example.taskmanager.repositories;

import com.example.taskmanager.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Здесь вы можете добавлять дополнительные методы для поиска задач, если это необходимо
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndReady(Long userId, boolean ready);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.ready = true")
    List<Task> findArchivedTasksByUserId(@Param("userId") Long userId);

}