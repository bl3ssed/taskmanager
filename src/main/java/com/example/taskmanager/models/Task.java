package com.example.taskmanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Изменено на маленькую букву

    private String title; // Изменено на маленькую букву
    private String description; // Изменено на маленькую букву
    private boolean ready; // Изменено на маленькую букву

    // Конструктор без параметров
    public Task() {}

    // Полный конструктор
    public Task(Long id, String title, String description, boolean ready) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ready = ready;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}