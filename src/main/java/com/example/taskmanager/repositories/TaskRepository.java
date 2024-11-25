package com.example.taskmanager.repositories;

import com.example.taskmanager.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    void deleteAllByReadyTrue();
    // Здесь вы можете добавлять дополнительные методы для поиска задач, если это необходимо
}