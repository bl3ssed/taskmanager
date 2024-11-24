package com.example.taskmanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseCheckService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean checkDatabaseConnection() {
        try {
            // Выполняем простой запрос
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true; // Подключение успешно
        } catch (Exception e) {
            e.printStackTrace(); // Выводим ошибку в консоль
            return false; // Ошибка подключения
        }
    }

    public String getCurrentDatabaseName() {
        try {
            // Выполняем запрос для получения имени текущей базы данных
            return jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        } catch (Exception e) {
            e.printStackTrace(); // Выводим ошибку в консоль
            return null; // Ошибка получения имени базы данных
        }
    }
}