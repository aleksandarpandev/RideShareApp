package com.carpool.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReservationCreateDto {
    
    @NotNull(message = "Ride ID is required")
    private Long rideId;
    
    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Number of seats must be at least 1")
    private Integer seatsReserved;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    // Constructors
    public ReservationCreateDto() {}
    
    public ReservationCreateDto(Long rideId, Integer seatsReserved, String notes) {
        this.rideId = rideId;
        this.seatsReserved = seatsReserved;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }
    
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    
    public Integer getSeatsReserved() {
        return seatsReserved;
    }
    
    public void setSeatsReserved(Integer seatsReserved) {
        this.seatsReserved = seatsReserved;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
