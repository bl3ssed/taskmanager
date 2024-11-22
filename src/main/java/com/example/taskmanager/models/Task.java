package com.example.taskmanager.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
public class Task {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long Id;
    public String Title;
    public String Description;
    public boolean Ready;

    public Task(Long id, String title, String description, boolean ready) {
        Id = id;
        Title = title;
        Description = description;
        Ready = ready;
    }

    public Long getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public boolean isReady() {
        return Ready;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setReady(boolean ready) {
        Ready = ready;
    }
}
