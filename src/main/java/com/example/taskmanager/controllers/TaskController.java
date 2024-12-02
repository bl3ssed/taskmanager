package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Получение всех задач конкретного пользователя
    @GetMapping("/user/{userId}")
    public String getAllTasksByUser(@PathVariable Long userId, Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userService.findByUsername(currentUsername);

        if (currentUser.isEmpty() || !currentUser.get().getId().equals(userId)) {
            return "error/403"; // Возвращаем ошибку доступа, если пользователь не совпадает
        }

        // Получение задач пользователя
        List<Task> activeTasks = taskService.getTasksByUserIdAndReady(userId, false); // Задачи, которые не готовы
        List<Task> archivedTasks = taskService.getTasksByUserIdAndReady(userId, true); // Архивированные задачи (готовые)

        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("archivedTasks", archivedTasks);
        model.addAttribute("userId", userId);

        return "taskList"; // Шаблон отображения списка задач
    }


    // Создание задачи для пользователя
    @PostMapping("/user/{userId}")
    public String createTaskForUser(@PathVariable Long userId, @ModelAttribute Task task) {
        taskService.createTaskForUser(task, userId);
        return "redirect:/tasks/user/" + userId; // Перенаправляем на список задач пользователя
    }

    // Получение задачи по ID для редактирования (только для указанного пользователя)
    @GetMapping("/{taskId}/user/{userId}")
    public String getTaskByIdAndUser(@PathVariable Long taskId, @PathVariable Long userId, Model model) {
        taskService.getTaskByIdAndUser(taskId, userId).ifPresent(task -> model.addAttribute("task", task));
        model.addAttribute("userId", userId); // Для корректного возвращения пользователя
        return "editTask"; // Шаблон для редактирования задачи
    }
    @PatchMapping("/{taskId}/user/{userId}/ready")
    public ResponseEntity<Void> setTaskReady(@PathVariable Long taskId, @PathVariable Long userId) {
        // Логика обновления статуса задачи
        taskService.setReady(taskId, userId);
        return ResponseEntity.ok().build();
    }


    // Обновление задачи для пользователя
    @PostMapping("/{taskId}/user/{userId}")
    public String updateTaskForUser(@PathVariable Long taskId, @PathVariable Long userId, @ModelAttribute Task updatedTask) {
        taskService.updateTaskForUser(taskId, updatedTask, userId);
        return "redirect:/tasks/user/" + userId; // Перенаправляем на список задач пользователя
    }

    // Удаление задачи пользователя
    @DeleteMapping("/{taskId}/user/{userId}")
    public ResponseEntity<Void> deleteTaskForUser(@PathVariable Long taskId, @PathVariable Long userId) {
        taskService.deleteTaskForUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/user/{userId}/archive")
    public ResponseEntity<Void> clearUserArchive(@PathVariable Long userId, Authentication authentication) {
        String currentUsername = authentication.getName();

        // Проверяем, что текущий пользователь соответствует userId
        User currentUser = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Доступ запрещен
        }

        taskService.clearArchiveByUser(userId);
        return ResponseEntity.noContent().build();
    }

}
