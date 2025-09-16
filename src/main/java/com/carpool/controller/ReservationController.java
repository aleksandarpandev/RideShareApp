package com.carpool.controller;

import com.carpool.dto.ReservationCreateDto;
import com.carpool.dto.ReservationResponseDto;
import com.carpool.security.UserPrincipal;
import com.carpool.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<ReservationResponseDto> createReservation(@Valid @RequestBody ReservationCreateDto reservationDto,
                                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReservationResponseDto reservation = reservationService.createReservation(reservationDto, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
    
    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByUser(userPrincipal.getId());
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<List<ReservationResponseDto>> getUpcomingReservations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ReservationResponseDto> reservations = reservationService.getUpcomingReservationsByUser(userPrincipal.getId());
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/past")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<List<ReservationResponseDto>> getPastReservations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ReservationResponseDto> reservations = reservationService.getPastReservationsByUser(userPrincipal.getId());
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/ride/{rideId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByRide(@PathVariable Long rideId) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByRide(rideId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<ReservationResponseDto> getReservationById(@PathVariable Long id) {
        ReservationResponseDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }
    
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<ReservationResponseDto> cancelReservation(@PathVariable Long id,
                                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReservationResponseDto reservation = reservationService.cancelReservation(id, userPrincipal.getId());
        return ResponseEntity.ok(reservation);
    }
}
