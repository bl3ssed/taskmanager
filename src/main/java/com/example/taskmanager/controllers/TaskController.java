package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

        if (currentUser.isPresent() && !currentUser.get().getId().equals(userId)) {
            // Если текущий пользователь не является владельцем запрашиваемых задач, отказать в доступе
            return "error/403"; // Можно перенаправить на страницу с ошибкой доступа
        }

        List<Task> tasks = taskService.getAllTasksByUserId(userId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("userId", userId);
        return "taskList"; // Шаблон для отображения задач
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
}
