package com.carpool.repository;

import com.carpool.entity.Ride;
import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    /**
     * Find rides by origin and destination (case-insensitive)
     */
    @Query("SELECT r FROM Ride r WHERE " +
           "LOWER(r.origin) LIKE LOWER(CONCAT('%', :origin, '%')) AND " +
           "LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%')) AND " +
           "r.status = 'ACTIVE' AND r.availableSeats > 0 AND r.dateTime > :currentTime " +
           "ORDER BY r.dateTime ASC")
    List<Ride> findByOriginAndDestination(@Param("origin") String origin, 
                                         @Param("destination") String destination,
                                         @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find rides by origin, destination and date
     */
    @Query("SELECT r FROM Ride r WHERE " +
           "LOWER(r.origin) LIKE LOWER(CONCAT('%', :origin, '%')) AND " +
           "LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%')) AND " +
           "DATE(r.dateTime) = DATE(:date) AND " +
           "r.status = 'ACTIVE' AND r.availableSeats > 0 AND r.dateTime > :currentTime " +
           "ORDER BY r.dateTime ASC")
    List<Ride> findByOriginDestinationAndDate(@Param("origin") String origin, 
                                             @Param("destination") String destination,
                                             @Param("date") LocalDateTime date,
                                             @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find all active rides with available seats
     */
    @Query("SELECT r FROM Ride r WHERE r.status = 'ACTIVE' AND r.availableSeats > 0 AND r.dateTime > :currentTime ORDER BY r.dateTime ASC")
    List<Ride> findActiveRidesWithAvailableSeats(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find rides by driver
     */
    List<Ride> findByDriverOrderByDateTimeDesc(User driver);
    
    /**
     * Find rides by driver and status
     */
    List<Ride> findByDriverAndStatusOrderByDateTimeDesc(User driver, Ride.RideStatus status);
    
    /**
     * Find upcoming rides for a driver
     */
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver AND r.dateTime > :currentTime ORDER BY r.dateTime ASC")
    List<Ride> findUpcomingRidesByDriver(@Param("driver") User driver, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find past rides for a driver
     */
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver AND r.dateTime < :currentTime ORDER BY r.dateTime DESC")
    List<Ride> findPastRidesByDriver(@Param("driver") User driver, @Param("currentTime") LocalDateTime currentTime);
}
