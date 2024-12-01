package com.example.taskmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("message", "An error occurred. Please try again.");
        return "errorPage"; // Шаблон для отображения ошибок
    }
}

