package com.example.mongodbdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/info")
    public Map<String, String> getPublicInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Это публичная информация, доступная без авторизации");
        response.put("version", "2.0.0");
        response.put("status", "active");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "MongoDB Demo Application with Method Security");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return health;
    }

    @GetMapping("/features")
    public Map<String, Object> getFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("database", "MongoDB");
        features.put("security", "Spring Security Method Level Security");
        features.put("authentication", "Form Login");
        features.put("method_annotations", new String[]{
            "@Secured",
            "@RolesAllowed (JSR-250)",
            "@PreAuthorize / @PostAuthorize",
            "@PreFilter / @PostFilter"
        });
        features.put("available_roles", new String[]{"READ", "WRITE", "DELETE"});
        features.put("test_users", Map.of(
            "reader", "reader (только READ)",
            "writer", "writer (только WRITE)",
            "deleter", "deleter (только DELETE)",
            "editor", "editor (READ и WRITE)",
            "admin", "admin (READ, WRITE, DELETE)"
        ));
        return features;
    }
}