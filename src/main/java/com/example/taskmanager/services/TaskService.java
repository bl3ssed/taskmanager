package com.example.taskmanager.services;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Получить все задачи
    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        System.out.println("Retrieved tasks: " + tasks); // Выводим задачи в консоль
        return tasks;
    }

    // Получить задачу по ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Создать новую задачу
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Обновить существующую задачу
    public Task updateTask(Long id, Task updatedTask) {
        if (taskRepository.existsById(id)) {
            updatedTask.setId(id);
            return taskRepository.save(updatedTask);
        }
        return null; // Или выбросьте исключение, если задача не найдена
    }

    // Удалить задачу по ID
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}