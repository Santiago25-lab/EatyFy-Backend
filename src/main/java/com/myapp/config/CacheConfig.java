package com.myapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Simple in-memory cache for development
        // In production, use Redis or another distributed cache
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
            "restaurants",
            "reviews",
            "promotions",
            "notifications",
            "userPreferences"
        );
        return cacheManager;
    }

    /*
    Cache Strategy for EatyFy:

    1. restaurants - Cache restaurant listings by city/budget (TTL: 30 minutes)
    2. reviews - Cache reviews for restaurants (TTL: 15 minutes)
    3. promotions - Cache active promotions (TTL: 1 hour)
    4. notifications - Cache user notifications (TTL: 5 minutes)
    5. userPreferences - Cache user preference data (TTL: 24 hours)

    Future implementation: Redis cache with proper TTL and eviction policies
    */
}