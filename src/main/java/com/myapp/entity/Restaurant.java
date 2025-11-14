package com.myapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

// Entity: Represents a restaurant in the app
@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the restaurant

    @NotBlank
    private String name;        // Restaurant's name

    @NotBlank
    private String address;     // Restaurant's address

    private String cuisineType; // Type of cuisine (Italian, Mexican, etc.)

    private Double averagePricePerPerson;
    // Average price per person, useful for the budget feature

    private String phone;       // Contact phone
    private String website;     // Website URL
    private String openingHours; // Opening hours

    private Double latitude;    // For map
    private Double longitude;   // For map

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;         // Restaurant owner

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public Double getAveragePricePerPerson() {
        return averagePricePerPerson;
    }

    public void setAveragePricePerPerson(Double averagePricePerPerson) {
        this.averagePricePerPerson = averagePricePerPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
