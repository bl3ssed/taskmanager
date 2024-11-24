package com.example.taskmanager.controllers;

import com.example.taskmanager.services.DatabaseCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class HealthCheckController {

    @Autowired
    private DatabaseCheckService databaseCheckService;

    @GetMapping("/db")
    public ResponseEntity<String> checkDatabase() {
        boolean isConnected = databaseCheckService.checkDatabaseConnection();
        if (isConnected) {
            return ResponseEntity.ok("Database connection is successful.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to connect to the database.");
        }
    }

    @GetMapping("/current-database")
    public ResponseEntity<String> getCurrentDatabaseName() {
        String dbName = databaseCheckService.getCurrentDatabaseName();
        if (dbName != null) {
            return ResponseEntity.ok("Connected to database: " + dbName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve database name.");
        }
    }
}