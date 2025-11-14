package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.User;
import com.myapp.service.UserService;

// Controller for handling User-related HTTP requests
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users (admin only)
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    // Create a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // Get user by id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Get current user profile
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Update current user profile
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User updatedUser, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setName(updatedUser.getName());
        user.setFoodPreferences(updatedUser.getFoodPreferences());
        User saved = userService.saveUser(user);
        return ResponseEntity.ok(saved);
    }

    // Delete user by id
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
