package com.myapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/cities")
public class CitiesController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCities() {
        try {
            ClassPathResource resource = new ClassPathResource("cities-data.json");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
            @SuppressWarnings("unchecked")
            Map<String, Object> citiesData = (Map<String, Object>) objectMapper.readValue(content, Map.class);
            return ResponseEntity.ok(citiesData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCitiesList() {
        try {
            ClassPathResource resource = new ClassPathResource("cities-data.json");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
            Map<String, Object> citiesData = objectMapper.readValue(content, Map.class);
            return ResponseEntity.ok(citiesData.get("cities"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}