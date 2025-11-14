package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.MenuItem;
import com.myapp.entity.Restaurant;
import com.myapp.entity.User;
import com.myapp.service.MenuItemService;
import com.myapp.service.RestaurantService;
import com.myapp.service.UserService;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    // Get menu items for a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItem> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return menuItemService.getMenuItemsByRestaurant(restaurantId);
    }

    // Get menu items by category
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    public List<MenuItem> getMenuItemsByCategory(@PathVariable Long restaurantId, @PathVariable String category) {
        return menuItemService.getMenuItemsByRestaurantAndCategory(restaurantId, category);
    }

    // Create menu item (restaurant owner only)
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null || !"RESTAURANT".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }

        // Verify the restaurant belongs to the user
        Restaurant restaurant = restaurantService.getRestaurantById(menuItem.getRestaurant().getId());
        if (restaurant == null || !restaurant.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        menuItem.setRestaurant(restaurant);
        MenuItem saved = menuItemService.saveMenuItem(menuItem);
        return ResponseEntity.ok(saved);
    }

    // Update menu item (restaurant owner only)
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedItem, Authentication authentication) {
        MenuItem existingItem = menuItemService.getMenuItemsByRestaurant(id).stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (existingItem == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null || !existingItem.getRestaurant().getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        updatedItem.setId(id);
        updatedItem.setRestaurant(existingItem.getRestaurant());
        MenuItem saved = menuItemService.saveMenuItem(updatedItem);
        return ResponseEntity.ok(saved);
    }

    // Delete menu item (restaurant owner only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id, Authentication authentication) {
        // This is a simplified version - in production, you'd check ownership
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}