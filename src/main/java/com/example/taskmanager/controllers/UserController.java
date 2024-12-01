package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    // Регистрация нового пользователя
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Получаем информацию об аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем, аутентифицирован ли пользователь и не является ли он анонимным
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("Authenticated user detected, redirecting to /home");
            return "redirect:/home"; // Если пользователь уже авторизован, перенаправляем на главную
        }

        // Если пользователь не авторизован, показываем форму регистрации
        model.addAttribute("user", new User());
        return "registerUser"; // Шаблон для страницы регистрации
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        System.out.println("Reg try\n");
        userService.registerUser(user);
        return "redirect:/login"; // После успешной регистрации перенаправляем на страницу входа
    }


    // Авторизация (страница входа)
    @GetMapping("/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Логирование данных об аутентификации
        if (authentication != null) {
            System.out.println("Authentication details: " + authentication);
        }

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("User is already authenticated: " + authentication.getName() + ", redirecting to home");
            return "redirect:/home";
        }

        return "loginUser"; // Шаблон для страницы входа
    }






    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        // Эта часть не нужна, если Spring Security используется для аутентификации
        return "redirect:/home"; // Spring Security автоматически обработает аутентификацию
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        // Проверка на аутентификацию пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        // Получение имени пользователя из аутентификации
        String username = authentication.getName();

        // Поиск пользователя по имени
        Optional<User> user = userService.findByUsername(username);
        user.ifPresent(value -> {
            model.addAttribute("user", value);

            // Загрузка задач для текущего пользователя и добавление их в модель
            List<Task> tasks = taskService.getAllTasksByUserId(value.getId()); // Получаем задачи для пользователя
            model.addAttribute("tasks", tasks);
        });

        return "home"; // Шаблон home.html
    }





    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userService.findByUsername(username);
        user.ifPresent(value -> model.addAttribute("user", value));

        return "userProfile"; // Шаблон профиля
    }
    // Профиль пользователя
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "userProfile"; // Шаблон для отображения профиля пользователя
        } else {
            return "redirect:/error"; // Перенаправляем на страницу ошибки, если пользователь не найден
        }
    }

    // Обновление профиля пользователя
    @GetMapping("/{id}/edit")
    public String showEditProfileForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userService.getUserById(id);
        if (user.isPresent() && user.get().getUsername().equals(username)) {
            model.addAttribute("user", user.get());
            return "editUser";
        }

        return "redirect:/error"; // Возвращаем ошибку, если пользователь не имеет доступа
    }



    @PostMapping("/{id}/edit")
    public String updateUserProfile(@PathVariable Long id, @ModelAttribute User updatedUser) {
        userService.updateUser(id, updatedUser);
        return "redirect:/users/" + id; // Перенаправляем на профиль пользователя
    }

    // Удаление пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty() || !user.get().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Доступ запрещен
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Успешное удаление
    }


}
