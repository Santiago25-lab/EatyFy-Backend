package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.Promotion;
import com.myapp.entity.Restaurant;
import com.myapp.service.PromotionService;
import com.myapp.service.RestaurantService;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private RestaurantService restaurantService;

    // Get all promotions
    @GetMapping
    public List<Promotion> getPromotions(@RequestParam(required = false) String city) {
        if (city != null) {
            return promotionService.getPromotionsByCity(city);
        }
        return promotionService.getAllPromotions();
    }

    // Get promotion by id
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotion(@PathVariable Long id) {
        return promotionService.getAllPromotions().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create promotion (admin or restaurant owner)
    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        if (promotion.getRestaurant() != null) {
            Restaurant restaurant = restaurantService.getRestaurantById(promotion.getRestaurant().getId());
            if (restaurant == null) {
                return ResponseEntity.badRequest().build();
            }
            promotion.setRestaurant(restaurant);
        }
        Promotion saved = promotionService.savePromotion(promotion);
        return ResponseEntity.ok(saved);
    }

    // Update promotion
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @RequestBody Promotion updatedPromotion) {
        Promotion promotion = promotionService.getAllPromotions().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);

        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }

        updatedPromotion.setId(id);
        Promotion saved = promotionService.savePromotion(updatedPromotion);
        return ResponseEntity.ok(saved);
    }

    // Delete promotion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        Promotion promotion = promotionService.getAllPromotions().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);

        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }

        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}