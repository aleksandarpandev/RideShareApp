package com.carpool.dto;

import com.carpool.entity.Reservation;

import java.time.LocalDateTime;

public class ReservationResponseDto {
    
    private Long id;
    private RideResponseDto ride;
    private UserResponseDto user;
    private Integer seatsReserved;
    private Reservation.ReservationStatus status;
    private String notes;
    private LocalDateTime createdAt;
    
    // Constructors
    public ReservationResponseDto() {}
    
    public ReservationResponseDto(Reservation reservation) {
        this.id = reservation.getId();
        this.ride = new RideResponseDto(reservation.getRide());
        this.user = new UserResponseDto(reservation.getUser());
        this.seatsReserved = reservation.getSeatsReserved();
        this.status = reservation.getStatus();
        this.notes = reservation.getNotes();
        this.createdAt = reservation.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RideResponseDto getRide() {
        return ride;
    }
    
    public void setRide(RideResponseDto ride) {
        this.ride = ride;
    }
    
    public UserResponseDto getUser() {
        return user;
    }
    
    public void setUser(UserResponseDto user) {
        this.user = user;
    }
    
    public Integer getSeatsReserved() {
        return seatsReserved;
    }
    
    public void setSeatsReserved(Integer seatsReserved) {
        this.seatsReserved = seatsReserved;
    }
    
    public Reservation.ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(Reservation.ReservationStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
