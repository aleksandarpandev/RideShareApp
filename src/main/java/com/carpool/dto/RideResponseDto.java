package com.carpool.dto;

import com.carpool.entity.Ride;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideResponseDto {
    
    private Long id;
    private UserResponseDto driver;
    private String origin;
    private String destination;
    private LocalDateTime dateTime;
    private BigDecimal price;
    private Integer availableSeats;
    private Integer totalSeats;
    private String description;
    private Ride.RideStatus status;
    private LocalDateTime createdAt;
    
    // Constructors
    public RideResponseDto() {}
    
    public RideResponseDto(Ride ride) {
        this.id = ride.getId();
        this.driver = new UserResponseDto(ride.getDriver());
        this.origin = ride.getOrigin();
        this.destination = ride.getDestination();
        this.dateTime = ride.getDateTime();
        this.price = ride.getPrice();
        this.availableSeats = ride.getAvailableSeats();
        this.totalSeats = ride.getTotalSeats();
        this.description = ride.getDescription();
        this.status = ride.getStatus();
        this.createdAt = ride.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserResponseDto getDriver() {
        return driver;
    }
    
    public void setDriver(UserResponseDto driver) {
        this.driver = driver;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
    
    public Integer getTotalSeats() {
        return totalSeats;
    }
    
    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Ride.RideStatus getStatus() {
        return status;
    }
    
    public void setStatus(Ride.RideStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
