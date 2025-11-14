package com.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myapp.entity.User;
import com.myapp.repository.UserRepository;

// Service to handle User entity operations
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Save a new user or update an existing user
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Get user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    // Delete user by id
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
