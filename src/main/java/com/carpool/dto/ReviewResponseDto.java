package com.carpool.dto;

import com.carpool.entity.Review;

import java.time.LocalDateTime;

public class ReviewResponseDto {
    
    private Long id;
    private Long rideId;
    private UserResponseDto reviewer;
    private UserResponseDto driver;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    
    // Constructors
    public ReviewResponseDto() {}
    
    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.rideId = review.getRide().getId();
        this.reviewer = new UserResponseDto(review.getReviewer());
        this.driver = new UserResponseDto(review.getDriver());
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRideId() {
        return rideId;
    }
    
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    
    public UserResponseDto getReviewer() {
        return reviewer;
    }
    
    public void setReviewer(UserResponseDto reviewer) {
        this.reviewer = reviewer;
    }
    
    public UserResponseDto getDriver() {
        return driver;
    }
    
    public void setDriver(UserResponseDto driver) {
        this.driver = driver;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
