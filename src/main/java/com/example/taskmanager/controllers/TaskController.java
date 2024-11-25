package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();

        // Фильтрация задач на активные и архивные
        List<Task> archivedTasks = tasks.stream()
                .filter(Task::isReady) // Используем метод isReady()
                .collect(Collectors.toList());

        List<Task> activeTasks = tasks.stream()
                .filter(task -> !task.isReady())
                .collect(Collectors.toList());

        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("archivedTasks", archivedTasks);

        return "taskList"; // Возвращаем имя шаблона
    }

    @DeleteMapping("/archive")
    public ResponseEntity<Void> clearArchive() {
        taskService.clearArchive();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ready")
    public ResponseEntity<Void> setTaskReady(@PathVariable Long id) {
        taskService.setTaskReady(id); // Метод в сервисе для обновления статуса задачи
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public String createTask(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/tasks"; // Перенаправление на список задач после создания
    }

    @GetMapping("/{id}")
    public String getTaskById(@PathVariable Long id, Model model) {
        taskService.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
        return "editTask"; // Здесь можно создать отдельный шаблон для редактирования задачи
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask) {
        taskService.updateTask(id, updatedTask);
        return "redirect:/tasks"; // Перенаправление на список задач после обновления
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}