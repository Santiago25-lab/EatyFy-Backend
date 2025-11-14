package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.Notification;
import com.myapp.entity.User;
import com.myapp.service.NotificationService;
import com.myapp.service.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // Get notifications for current user
    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return List.of();
        }
        return notificationService.getNotificationsByUser(user.getId());
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        Notification notification = notificationService.getNotificationsByUser(0L).stream()
                .filter(n -> n.getId().equals(id))
                .findFirst().orElse(null);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        notification.setRead(true);
        notificationService.saveNotification(notification);
        return ResponseEntity.ok().build();
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Authentication authentication) {
        Notification notification = notificationService.getNotificationsByUser(0L).stream()
                .filter(n -> n.getId().equals(id))
                .findFirst().orElse(null);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}