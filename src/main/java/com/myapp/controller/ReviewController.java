package com.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myapp.entity.Review;
import com.myapp.entity.Restaurant;
import com.myapp.entity.User;
import com.myapp.service.ReviewService;
import com.myapp.service.RestaurantService;
import com.myapp.service.UserService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    // Get reviews for a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public List<Review> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        return reviewService.getReviewsByRestaurant(restaurantId);
    }

    // Get reviews by current user
    @GetMapping("/my")
    public List<Review> getMyReviews(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return List.of();
        }
        return reviewService.getReviewsByUser(user.getId());
    }

    // Create a review
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Restaurant restaurant = restaurantService.getRestaurantById(review.getRestaurant().getId());
        if (restaurant == null) {
            return ResponseEntity.badRequest().build();
        }

        review.setUser(user);
        review.setRestaurant(restaurant);
        Review saved = reviewService.saveReview(review);
        return ResponseEntity.ok(saved);
    }

    // Delete a review (user can delete their own)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, Authentication authentication) {
        Review review = reviewService.getReviewsByUser(0L).stream()
                .filter(r -> r.getId().equals(id))
                .findFirst().orElse(null);

        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null || !review.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}