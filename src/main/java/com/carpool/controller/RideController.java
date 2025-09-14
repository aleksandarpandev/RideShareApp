package com.carpool.controller;

import com.carpool.dto.RideCreateDto;
import com.carpool.dto.RideResponseDto;
import com.carpool.entity.Ride;
import com.carpool.security.UserPrincipal;
import com.carpool.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rides")
public class RideController {
    
    @Autowired
    private RideService rideService;
    
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponseDto> createRide(@Valid @RequestBody RideCreateDto rideCreateDto,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RideResponseDto rideResponse = rideService.createRide(rideCreateDto, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rideResponse);
    }
    
    @GetMapping
    public ResponseEntity<List<RideResponseDto>> getAllRides() {
        List<RideResponseDto> rides = rideService.getAllActiveRides();
        return ResponseEntity.ok(rides);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<RideResponseDto>> searchRides(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        
        if (origin == null || destination == null) {
            List<RideResponseDto> rides = rideService.getAllActiveRides();
            return ResponseEntity.ok(rides);
        }
        
        List<RideResponseDto> rides = rideService.searchRides(origin, destination, date);
        return ResponseEntity.ok(rides);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRideById(@PathVariable Long id) {
        RideResponseDto ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }
    
    @GetMapping("/driver/my-rides")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RideResponseDto>> getMyRides(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RideResponseDto> rides = rideService.getRidesByDriver(userPrincipal.getId());
        return ResponseEntity.ok(rides);
    }
    
    @GetMapping("/driver/upcoming")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RideResponseDto>> getUpcomingRides(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RideResponseDto> rides = rideService.getUpcomingRidesByDriver(userPrincipal.getId());
        return ResponseEntity.ok(rides);
    }
    
    @GetMapping("/driver/past")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RideResponseDto>> getPastRides(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RideResponseDto> rides = rideService.getPastRidesByDriver(userPrincipal.getId());
        return ResponseEntity.ok(rides);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponseDto> updateRideStatus(@PathVariable Long id,
                                                           @RequestParam Ride.RideStatus status,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RideResponseDto ride = rideService.updateRideStatus(id, status, userPrincipal.getId());
        return ResponseEntity.ok(ride);
    }
}
