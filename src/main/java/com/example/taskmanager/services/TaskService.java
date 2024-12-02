package com.example.taskmanager.services;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    public List<Task> getTasksByUserIdAndReady(Long userId, boolean ready) {
        return taskRepository.findByUserIdAndReady(userId, ready);
    }

    // Получить все задачи пользователя
    public List<Task> getAllTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Получить задачу по ID (проверяя владельца)
    public Optional<Task> getTaskByIdAndUser(Long taskId, Long userId) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(userId));
    }

    // Создать задачу для пользователя
    public Task createTaskForUser(Task task, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            task.setUser(user.get());
            return taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
    }

    // Обновить задачу пользователя
    public Task updateTaskForUser(Long taskId, Task updatedTask, Long userId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent() && taskOptional.get().getUser().getId().equals(userId)) {
            updatedTask.setId(taskId);
            updatedTask.setUser(taskOptional.get().getUser()); // Сохраняем связь с пользователем
            return taskRepository.save(updatedTask);
        } else {
            throw new IllegalArgumentException("Задача не найдена или принадлежит другому пользователю");
        }
    }

    // Удалить задачу пользователя
    public void deleteTaskForUser(Long taskId, Long userId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent() && taskOptional.get().getUser().getId().equals(userId)) {
            taskRepository.deleteById(taskId);
        } else {
            throw new IllegalArgumentException("Задача не найдена или принадлежит другому пользователю");
        }
    }

    public void setTaskReady(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setReady(true); // Устанавливаем статус ready в true
            taskRepository.save(task); // Сохраняем изменения
        } else {
            throw new EntityNotFoundException("Task not found with id " + id);
        }
    }

    public void setReady(Long taskId, Long userId) {
        // Проверяем, существует ли задача с таким ID и принадлежит ли она пользователю
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("Task does not belong to this user");
        }

        // Обновляем статус задачи
        task.setReady(true);
        taskRepository.save(task); // Сохраняем изменения
    }

    public void clearArchiveByUser(Long userId) {
        List<Task> archivedTasks = taskRepository.findArchivedTasksByUserId(userId);
        taskRepository.deleteAll(archivedTasks);
    }


}
