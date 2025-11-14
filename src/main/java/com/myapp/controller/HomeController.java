package com.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "Bienvenido a EatyFy API",
            "version", "1.0.0",
            "status", "running",
            "endpoints", Map.of(
                "auth", "/api/auth",
                "restaurants", "/api/restaurants",
                "reviews", "/api/reviews",
                "promotions", "/api/promotions",
                "notifications", "/api/notifications",
                "users", "/api/users",
                "test", "/api/test",
                "h2-console", "/h2-console"
            )
        ));
    }
}