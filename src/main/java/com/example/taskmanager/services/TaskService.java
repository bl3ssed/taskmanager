package com.example.taskmanager.services;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.repositories.UserRepository;
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
}
