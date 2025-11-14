package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.Restaurant;
import com.myapp.entity.User;
import com.myapp.service.RestaurantService;
import com.myapp.service.UserService;

// Controller for handling Restaurant-related HTTP requests
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    // Get all restaurants
    @GetMapping
    public List<Restaurant> getRestaurants(@RequestParam(required = false) String city,
                                           @RequestParam(required = false) Double budget,
                                           @RequestParam(required = false) String cuisine) {
        List<Restaurant> restaurants = restaurantService.getRestaurants();

        if (city != null && !city.isEmpty()) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAddress().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }

        if (budget != null && budget > 0) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAveragePricePerPerson() != null && r.getAveragePricePerPerson() <= budget)
                    .toList();
        }

        if (cuisine != null && !cuisine.isEmpty()) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getCuisineType() != null && r.getCuisineType().toLowerCase().contains(cuisine.toLowerCase()))
                    .toList();
        }

        return restaurants;
    }

    // Create a new restaurant (for restaurant owners)
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !"RESTAURANT".equals(user.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        restaurant.setOwner(user);
        Restaurant saved = restaurantService.saveRestaurant(restaurant);
        return ResponseEntity.ok(saved);
    }

    // Get restaurant by id
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(restaurant);
    }

    // Update restaurant (owner only)
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant updatedRestaurant, Authentication authentication) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !restaurant.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        updatedRestaurant.setId(id);
        updatedRestaurant.setOwner(user);
        Restaurant saved = restaurantService.saveRestaurant(updatedRestaurant);
        return ResponseEntity.ok(saved);
    }

    // Delete restaurant by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id, Authentication authentication) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !restaurant.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
