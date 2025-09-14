package com.carpool.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideCreateDto {
    
    @NotBlank(message = "Origin is required")
    @Size(max = 255, message = "Origin must not exceed 255 characters")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    @Size(max = 255, message = "Destination must not exceed 255 characters")
    private String destination;
    
    @NotNull(message = "Date and time is required")
    @Future(message = "Date and time must be in the future")
    private LocalDateTime dateTime;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;
    
    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "Available seats must be at least 1")
    @Max(value = 8, message = "Available seats cannot exceed 8")
    private Integer availableSeats;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    // Constructors
    public RideCreateDto() {}
    
    public RideCreateDto(String origin, String destination, LocalDateTime dateTime, 
                        BigDecimal price, Integer availableSeats, String description) {
        this.origin = origin;
        this.destination = destination;
        this.dateTime = dateTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.description = description;
    }
    
    // Getters and Setters
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
