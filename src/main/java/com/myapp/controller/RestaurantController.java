package com.myapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.entity.Restaurant;
import com.myapp.entity.User;
import com.myapp.service.GeocodingService;
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

    @Autowired
    private GeocodingService geocodingService;

    // Get all restaurants from database
    @GetMapping
    public List<Restaurant> getRestaurants(@RequestParam(required = false) String city,
                                           @RequestParam(required = false) Double budget) {
        List<Restaurant> restaurants = restaurantService.getRestaurants();

        if (city != null) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAddress() != null &&
                               r.getAddress().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }

        if (budget != null) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAveragePricePerPerson() != null &&
                               r.getAveragePricePerPerson() <= budget)
                    .toList();
        }

        return restaurants;
    }

    // Get restaurants by city (legacy endpoint for frontend compatibility)
    @GetMapping("/search")
    public List<Map<String, Object>> getRestaurantsByCity(@RequestParam(required = false) String city,
                                                          @RequestParam(required = false) Double budget) {
        List<Restaurant> restaurants = restaurantService.getRestaurants();

        if (city != null) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAddress() != null &&
                               r.getAddress().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }

        if (budget != null) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getAveragePricePerPerson() != null &&
                               r.getAveragePricePerPerson() <= budget)
                    .toList();
        }

        // Convert to frontend format
        return restaurants.stream().<Map<String, Object>>map(r -> Map.of(
            "id", r.getId().toString(),
            "name", r.getName(),
            "address", r.getAddress(),
            "city", city != null ? city : "Bogotá",
            "lat", r.getLatitude() != null ? r.getLatitude() : 4.711,
            "lon", r.getLongitude() != null ? r.getLongitude() : -74.0721,
            "cuisine", r.getCuisineType() != null ? r.getCuisineType() : "No especificado",
            "phone", r.getPhone(),
            "website", r.getWebsite(),
            "priceRange", r.getAveragePricePerPerson() != null ?
                (r.getAveragePricePerPerson() < 30000 ? "$" :
                 r.getAveragePricePerPerson() < 60000 ? "$$" : "$$$") : "$$"
        )).toList();
    }

    // Create a new restaurant (for restaurant owners)
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !"RESTAURANT".equals(user.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        // Get coordinates from address using OpenStreetMap (optional)
        if (restaurant.getAddress() != null && !restaurant.getAddress().trim().isEmpty()) {
            try {
                Map<String, Double> coordinates = geocodingService.getCoordinates(restaurant.getAddress() + ", Colombia");
                if (coordinates != null && coordinates.get("lat") != null && coordinates.get("lon") != null) {
                    restaurant.setLatitude(coordinates.get("lat"));
                    restaurant.setLongitude(coordinates.get("lon"));
                }
            } catch (Exception e) {
                // Log error but don't fail restaurant creation
                System.err.println("Error geocoding address: " + restaurant.getAddress() + " - " + e.getMessage());
            }
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update coordinates if address changed
        if (!restaurant.getAddress().equals(updatedRestaurant.getAddress())) {
            Map<String, Double> coordinates = geocodingService.getCoordinates(updatedRestaurant.getAddress() + ", Colombia");
            if (coordinates != null) {
                updatedRestaurant.setLatitude(coordinates.get("lat"));
                updatedRestaurant.setLongitude(coordinates.get("lon"));
            }
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
