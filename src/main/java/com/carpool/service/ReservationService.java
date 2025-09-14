package com.carpool.service;

import com.carpool.dto.ReservationCreateDto;
import com.carpool.dto.ReservationResponseDto;
import com.carpool.entity.Reservation;
import com.carpool.entity.Ride;
import com.carpool.entity.User;
import com.carpool.exception.BusinessException;
import com.carpool.exception.ResourceNotFoundException;
import com.carpool.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final RideService rideService;
    private final UserService userService;
    
    @Autowired
    public ReservationService(ReservationRepository reservationRepository, 
                             RideService rideService, 
                             UserService userService) {
        this.reservationRepository = reservationRepository;
        this.rideService = rideService;
        this.userService = userService;
    }
    
    /**
     * Create a new reservation
     */
    public ReservationResponseDto createReservation(ReservationCreateDto reservationDto, Long userId) {
        User user = userService.findById(userId);
        Ride ride = rideService.findById(reservationDto.getRideId());
        
        // Validate reservation
        validateReservation(ride, user, reservationDto.getSeatsReserved());
        
        // Check if user already has a reservation for this ride
        Optional<Reservation> existingReservation = reservationRepository.findByRideAndUser(ride, user);
        if (existingReservation.isPresent()) {
            throw new BusinessException("You have already reserved seats for this ride");
        }
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setRide(ride);
        reservation.setUser(user);
        reservation.setSeatsReserved(reservationDto.getSeatsReserved());
        reservation.setNotes(reservationDto.getNotes());
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Update available seats in ride
        rideService.reduceAvailableSeats(ride.getId(), reservationDto.getSeatsReserved());
        
        return new ReservationResponseDto(savedReservation);
    }
    
    /**
     * Get reservations by user
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservationsByUser(Long userId) {
        User user = userService.findById(userId);
        List<Reservation> reservations = reservationRepository.findByUserOrderByCreatedAtDesc(user);
        
        return reservations.stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get upcoming reservations by user
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getUpcomingReservationsByUser(Long userId) {
        User user = userService.findById(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findUpcomingReservationsByUser(user, currentTime);
        
        return reservations.stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get past reservations by user
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getPastReservationsByUser(Long userId) {
        User user = userService.findById(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findPastReservationsByUser(user, currentTime);
        
        return reservations.stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reservations by ride
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservationsByRide(Long rideId) {
        Ride ride = rideService.findById(rideId);
        List<Reservation> reservations = reservationRepository.findByRideOrderByCreatedAtAsc(ride);
        
        return reservations.stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Cancel a reservation
     */
    public ReservationResponseDto cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        
        // Check if the user owns this reservation
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only cancel your own reservations");
        }
        
        // Check if reservation is already cancelled
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException("Reservation is already cancelled");
        }
        
        // Check if ride has already passed
        if (reservation.getRide().getDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot cancel reservation for past rides");
        }
        
        // Cancel reservation
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Restore available seats in ride
        rideService.increaseAvailableSeats(reservation.getRide().getId(), reservation.getSeatsReserved());
        
        return new ReservationResponseDto(savedReservation);
    }
    
    /**
     * Get reservation by ID
     */
    @Transactional(readOnly = true)
    public ReservationResponseDto getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        
        return new ReservationResponseDto(reservation);
    }
    
    /**
     * Validate reservation business rules
     */
    private void validateReservation(Ride ride, User user, Integer seatsRequested) {
        // Check if ride is active
        if (ride.getStatus() != Ride.RideStatus.ACTIVE) {
            throw new BusinessException("Cannot reserve seats for inactive rides");
        }
        
        // Check if ride is in the future
        if (ride.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot reserve seats for past rides");
        }
        
        // Check if enough seats are available
        if (ride.getAvailableSeats() < seatsRequested) {
            throw new BusinessException("Not enough available seats. Available: " + ride.getAvailableSeats());
        }
        
        // Check if user is not the driver
        if (ride.getDriver().getId().equals(user.getId())) {
            throw new BusinessException("Drivers cannot reserve seats in their own rides");
        }
        
        // Check if requested seats is valid
        if (seatsRequested <= 0 || seatsRequested > ride.getAvailableSeats()) {
            throw new BusinessException("Invalid number of seats requested");
        }
    }
}
