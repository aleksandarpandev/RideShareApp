package com.carpool.service;

import com.carpool.dto.RideCreateDto;
import com.carpool.dto.RideResponseDto;
import com.carpool.entity.Ride;
import com.carpool.entity.User;
import com.carpool.exception.ResourceNotFoundException;
import com.carpool.exception.UnauthorizedAccessException;
import com.carpool.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideService {
    
    private final RideRepository rideRepository;
    private final UserService userService;
    
    @Autowired
    public RideService(RideRepository rideRepository, UserService userService) {
        this.rideRepository = rideRepository;
        this.userService = userService;
    }
    
    /**
     * Create a new ride (driver only)
     */
    public RideResponseDto createRide(RideCreateDto rideCreateDto, Long driverId) {
        User driver = userService.findById(driverId);
        
        // Check if user is a driver
        if (driver.getRole() != User.Role.DRIVER) {
            throw new UnauthorizedAccessException("Only drivers can create rides");
        }
        
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setOrigin(rideCreateDto.getOrigin());
        ride.setDestination(rideCreateDto.getDestination());
        ride.setDateTime(rideCreateDto.getDateTime());
        ride.setPrice(rideCreateDto.getPrice());
        ride.setAvailableSeats(rideCreateDto.getAvailableSeats());
        ride.setTotalSeats(rideCreateDto.getAvailableSeats());
        ride.setDescription(rideCreateDto.getDescription());
        ride.setStatus(Ride.RideStatus.ACTIVE);
        
        Ride savedRide = rideRepository.save(ride);
        return new RideResponseDto(savedRide);
    }
    
    /**
     * Search rides by origin and destination
     */
    @Transactional(readOnly = true)
    public List<RideResponseDto> searchRides(String origin, String destination, LocalDateTime date) {
        List<Ride> rides;
        LocalDateTime currentTime = LocalDateTime.now();
        
        if (date != null) {
            rides = rideRepository.findByOriginDestinationAndDate(origin, destination, date, currentTime);
        } else {
            rides = rideRepository.findByOriginAndDestination(origin, destination, currentTime);
        }
        
        return rides.stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all active rides with available seats
     */
    @Transactional(readOnly = true)
    public List<RideResponseDto> getAllActiveRides() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ride> rides = rideRepository.findActiveRidesWithAvailableSeats(currentTime);
        
        return rides.stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get ride by ID
     */
    @Transactional(readOnly = true)
    public RideResponseDto getRideById(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        
        return new RideResponseDto(ride);
    }
    
    /**
     * Get rides by driver
     */
    @Transactional(readOnly = true)
    public List<RideResponseDto> getRidesByDriver(Long driverId) {
        User driver = userService.findById(driverId);
        List<Ride> rides = rideRepository.findByDriverOrderByDateTimeDesc(driver);
        
        return rides.stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get upcoming rides by driver
     */
    @Transactional(readOnly = true)
    public List<RideResponseDto> getUpcomingRidesByDriver(Long driverId) {
        User driver = userService.findById(driverId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ride> rides = rideRepository.findUpcomingRidesByDriver(driver, currentTime);
        
        return rides.stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get past rides by driver
     */
    @Transactional(readOnly = true)
    public List<RideResponseDto> getPastRidesByDriver(Long driverId) {
        User driver = userService.findById(driverId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ride> rides = rideRepository.findPastRidesByDriver(driver, currentTime);
        
        return rides.stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Update ride status
     */
    public RideResponseDto updateRideStatus(Long rideId, Ride.RideStatus status, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        
        // Check if the user is the driver of this ride
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new UnauthorizedAccessException("Only the driver can update ride status");
        }
        
        ride.setStatus(status);
        Ride savedRide = rideRepository.save(ride);
        return new RideResponseDto(savedRide);
    }
    
    /**
     * Reduce available seats when booking
     */
    public void reduceAvailableSeats(Long rideId, Integer seatsToReduce) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        
        if (ride.getAvailableSeats() < seatsToReduce) {
            throw new IllegalArgumentException("Not enough available seats");
        }
        
        ride.setAvailableSeats(ride.getAvailableSeats() - seatsToReduce);
        rideRepository.save(ride);
    }
    
    /**
     * Increase available seats when cancelling reservation
     */
    public void increaseAvailableSeats(Long rideId, Integer seatsToAdd) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        
        ride.setAvailableSeats(ride.getAvailableSeats() + seatsToAdd);
        rideRepository.save(ride);
    }
    
    /**
     * Get ride entity by ID (internal use)
     */
    @Transactional(readOnly = true)
    public Ride findById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
    }
}
