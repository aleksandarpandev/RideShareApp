package com.carpool.repository;

import com.carpool.entity.Reservation;
import com.carpool.entity.Ride;
import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find reservations by user
     */
    List<Reservation> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find reservations by user and status
     */
    List<Reservation> findByUserAndStatusOrderByCreatedAtDesc(User user, Reservation.ReservationStatus status);
    
    /**
     * Find reservations by ride
     */
    List<Reservation> findByRideOrderByCreatedAtAsc(Ride ride);
    
    /**
     * Find reservations by ride and status
     */
    List<Reservation> findByRideAndStatusOrderByCreatedAtAsc(Ride ride, Reservation.ReservationStatus status);
    
    /**
     * Check if user has already reserved a ride
     */
    Optional<Reservation> findByRideAndUser(Ride ride, User user);
    
    /**
     * Check if user has already reserved a ride with confirmed status
     */
    Optional<Reservation> findByRideAndUserAndStatus(Ride ride, User user, Reservation.ReservationStatus status);
    
    /**
     * Find upcoming reservations for a user
     */
    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND r.ride.dateTime > :currentTime AND r.status = 'CONFIRMED' ORDER BY r.ride.dateTime ASC")
    List<Reservation> findUpcomingReservationsByUser(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find past reservations for a user
     */
    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND r.ride.dateTime < :currentTime ORDER BY r.ride.dateTime DESC")
    List<Reservation> findPastReservationsByUser(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Count confirmed reservations for a ride
     */
    @Query("SELECT COALESCE(SUM(r.seatsReserved), 0) FROM Reservation r WHERE r.ride = :ride AND r.status = 'CONFIRMED'")
    Integer countConfirmedSeatsForRide(@Param("ride") Ride ride);
}
