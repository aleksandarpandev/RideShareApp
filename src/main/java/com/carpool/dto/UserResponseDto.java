package com.carpool.dto;

import com.carpool.entity.User;

public class UserResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private Double rating;
    private Integer totalReviews;
    
    // Constructors
    public UserResponseDto() {}
    
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.rating = user.getRating();
        this.totalReviews = user.getTotalReviews();
    }
    
    public UserResponseDto(Long id, String name, String email, User.Role role, Double rating, Integer totalReviews) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.rating = rating;
        this.totalReviews = totalReviews;
    }
    
    // Getters and Setters
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public User.Role getRole() {
        return role;
    }
    
    public void setRole(User.Role role) {
        this.role = role;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
}
